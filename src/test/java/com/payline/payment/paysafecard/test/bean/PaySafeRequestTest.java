package com.payline.payment.slimpay.test.bean;

import com.payline.payment.slimpay.bean.SlimpayPaymentRequest;
import com.payline.payment.slimpay.test.Utils;
import com.payline.payment.slimpay.utils.BadFieldException;
import com.payline.payment.slimpay.utils.InvalidRequestException;
import com.payline.payment.slimpay.utils.SlimpayCardConstants;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Currency;

public class PaySafeRequestTest {

    @Test
    public void createPaySafeRequestFromContractParam() throws InvalidRequestException {
        ContractParametersCheckRequest checkRequest = Utils.createContractParametersCheckRequest(null, null, null, Utils.AUTHORISATION_VAL);
        SlimpayPaymentRequest request = new SlimpayPaymentRequest(checkRequest);
        Assert.assertNotNull(request);
    }

    @Test
    public void createPaySafeRequestFromPaymentRequest() throws InvalidRequestException {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().build();
        SlimpayPaymentRequest request = new SlimpayPaymentRequest(paymentRequest);
        Assert.assertNotNull(request);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutAmount() throws InvalidRequestException {
        Amount amount = new Amount(null, Currency.getInstance("EUR"));
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withAmount(amount).build();
        new SlimpayPaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutCurrency() throws InvalidRequestException {
        Amount amount = new Amount(BigInteger.ONE, null);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withAmount(amount).build();
        new SlimpayPaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithNullSuccessUrl() throws InvalidRequestException {
        Environment environment = new Environment(Utils.NOTIFICATION_URL, null, Utils.FAILURE_URL, true);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withEnvironment(environment).build();
        new SlimpayPaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithNullFailureUrl() throws InvalidRequestException {
        Environment environment = new Environment(Utils.NOTIFICATION_URL, Utils.SUCCESS_URL, null, true);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withEnvironment(environment).build();
        new SlimpayPaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutAuthorisationKey() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, null, null, null);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        new SlimpayPaymentRequest(paymentRequest);
    }

    @Test
    public void createPaySafeRequestWithWrongCountryCode() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, null, "foo", Utils.AUTHORISATION_VAL);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        try {
            new SlimpayPaymentRequest(paymentRequest);
        }catch (BadFieldException e){
            Assert.assertEquals( SlimpayCardConstants.COUNTRYRESTRICTION_KEY, e.getField());
        }
    }

    @Test
    public void createPaySafeRequestWithBadMinAge() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, "a", null, Utils.AUTHORISATION_VAL);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        try {
            new SlimpayPaymentRequest(paymentRequest);
        } catch (BadFieldException e) {
            Assert.assertEquals(  SlimpayCardConstants.MINAGE_KEY, e.getField());
        }
    }

    @Test
    public void createPaySafeRequestWithOutOfRangeMinAge() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, "10000", null, Utils.AUTHORISATION_VAL);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        try {
            new SlimpayPaymentRequest(paymentRequest);
        } catch (BadFieldException e) {
            Assert.assertEquals(  SlimpayCardConstants.MINAGE_KEY, e.getField());
        }
    }

    @Test
    public void createAmount() {
        Assert.assertEquals("0.00", SlimpayPaymentRequest.createAmount(0));
        Assert.assertEquals("0.01", SlimpayPaymentRequest.createAmount(1));
        Assert.assertEquals("1.00", SlimpayPaymentRequest.createAmount(100));
        Assert.assertEquals("10.00", SlimpayPaymentRequest.createAmount(1000));
        Assert.assertEquals("100.00", SlimpayPaymentRequest.createAmount(10000));
    }

    @Test
    public void encode() {
        String s = "hello world";
        String s2 = "aGVsbG8gd29ybGQ=";
        Assert.assertEquals(s2, SlimpayPaymentRequest.encodeToBase64(s));
        Assert.assertEquals("", SlimpayPaymentRequest.encodeToBase64(""));
        Assert.assertEquals("", SlimpayPaymentRequest.encodeToBase64(null));
    }
}
