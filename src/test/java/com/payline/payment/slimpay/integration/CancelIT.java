package com.payline.payment.slimpay.integration;

import com.payline.payment.slimpay.business.impl.BeanAssemblerBusinessImpl;
import com.payline.payment.slimpay.service.impl.ConfigurationServiceImpl;
import com.payline.payment.slimpay.service.impl.PaymentServiceImpl;
import com.payline.payment.slimpay.service.impl.PaymentWithRedirectionServiceImpl;
import com.payline.payment.slimpay.service.impl.RefundServiceImpl;
import com.payline.payment.slimpay.utils.TestUtils;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.payline.payment.slimpay.utils.SlimpayConstants.*;


public class CancelIT extends AbstractPaymentIntegration {
    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();
    private RefundServiceImpl refundService = new RefundServiceImpl();


    private static final String MDP_IDENTIFIER = "SDD-Slimpay";

    private static final Map<String, String> PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        put(API_URL_KEY, "https://api.preprod.slimpay.com");
        put(API_PROFILE_KEY, "https://api.slimpay.net/alps/v1");
        put(API_NS_KEY, "https://api.slimpay.net/alps");
        put(APP_KEY, "monextreferral01");
    }};

    private static final Map<String, String> SENSITIVE_PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        put(APP_SECRET, "n32cXdaS0ZOACV8688ltKovAO6lquL4wKjZHnvyO");

    }};

    private static final ContractConfiguration CONTRACT_CONFIGURATION = new ContractConfiguration(MDP_IDENTIFIER, new HashMap<String, ContractProperty>() {{
        put(CREDITOR_REFERENCE_KEY, new ContractProperty("paylinemerchanttest1"));
        put(FIRST_PAYMENT_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
        put(MANDATE_PAYIN_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
        put(MANDATE_STANDARD_KEY, new ContractProperty("SEPA"));
        put(SIGNATURE_APPROVAL_METHOD, new ContractProperty("otp"));
        put(PAYMENT_PROCESSOR, new ContractProperty("slimpay"));
    }}
    );

    @Override
    protected Map<String, ContractProperty> generateParameterContract() {

        return CONTRACT_CONFIGURATION.getContractProperties();
    }

    @Override
    protected PaymentFormContext generatePaymentFormContext() {

        return PaymentFormContext.PaymentFormContextBuilder
                .aPaymentFormContext()
                .withPaymentFormParameter(PARTNER_CONFIGURATION_MAP)
                .withSensitivePaymentFormParameter(SENSITIVE_PARTNER_CONFIGURATION_MAP)
                .build();
    }

    @Override
    protected String payOnPartnerWebsite(String partnerUrl) {
        // Start browser
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        try {

            // Go to partner's website
            driver.get(partnerUrl);

            //entrer l'iban de test
            driver.findElement(By.xpath("//input[@name='iban']")).sendKeys("FR7600000000009915230573233");

            // validate iban click on bouton "continuer"
            driver.findElement(By.xpath("//input[@value='Continuer']")).click();

            // Wait for redirection to success or cancel url
            driver.findElement(By.xpath("//input[@class='button']")).click();

            //confirmation OTP
            driver.findElement(By.xpath("//*[@class='demo-tooltip demo-otp']//a")).click();
            String code = driver.findElement(By.xpath("//*[@class='demo-tooltip demo-otp']//a")).getText();
            driver.findElement(By.xpath("//input[@name='otp']")).sendKeys(code);


            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.findElement(By.xpath("//input[@class='button']")).click();

            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.or(ExpectedConditions.urlToBe(SUCCESS_URL), ExpectedConditions.urlToBe(CANCEL_URL)));


            return driver.getCurrentUrl();

        } finally {
            driver.quit();
        }

    }

    @Override
    protected String cancelOnPartnerWebsite(String s) {
        return null;
    }


    /**
     * For the cancel test to pass, for now, it is necessary to play it in debug and set the field executionDate
     * on the {@link com.payline.payment.slimpay.bean.common.Payment} bean
     * in {@link BeanAssemblerBusinessImpl#assemblePayin(PaymentRequest)}.
     *
     * The first block of code in this test case provide the string to pass to the method
     * {@link com.payline.payment.slimpay.bean.common.Payment.Builder#withExecutionDate(String)}, before the objet is built.
     */
    @Test
    public void fullCancelTest() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(tz);
        Calendar c = Calendar.getInstance();
        c.setTime( new Date());
        c.add(Calendar.DATE, 4);
        df.format( c.getTime() );

        //Make paymentRequest  with executionDate  = J+4
        PaymentRequest paymentRequest = createDefaultPaymentRequest();

        PaymentResponse paymentResponseFromPaymentRequest = paymentService.paymentRequest(paymentRequest);
        PaymentResponseRedirect paymentResponseRedirect = (PaymentResponseRedirect) paymentResponseFromPaymentRequest;
        String partnerUrl = paymentResponseRedirect.getRedirectionRequest().getUrl().toString();
        String redirectionUrl = this.payOnPartnerWebsite(partnerUrl);
        String partnerTransactionId = paymentResponseRedirect.getPartnerTransactionId();
        PaymentResponse paymentResponseFromFinalize = this.handlePartnerResponse(paymentWithRedirectionService, paymentRequest, paymentResponseRedirect);
        PaymentResponseSuccess paymentResponseSuccess = (PaymentResponseSuccess) paymentResponseFromFinalize;

        //CANCEL THE PAYMENT
        cancelPayment(paymentRequest, partnerTransactionId, paymentResponseSuccess.getTransactionAdditionalData());


    }


    @Override
    public PaymentRequest createDefaultPaymentRequest() {
        return TestUtils.createCompletePaymentBuilder().build();

    }

    private PaymentResponse handlePartnerResponse(PaymentWithRedirectionService paymentWithRedirectionService, PaymentRequest paymentRequest, PaymentResponseRedirect paymentResponseRedirect) {
        ContractConfiguration contractConfiguration = new ContractConfiguration("", this.generateParameterContract());
        Environment environment = new Environment("http://google.com/", "https://succesurl.com/", "http://localhost/cancelurl.com/", true);
        RedirectionPaymentRequest redirectionPaymentRequest = (RedirectionPaymentRequest)
                RedirectionPaymentRequest.builder()
                        .withContractConfiguration(contractConfiguration)
                        .withPaymentFormContext(this.generatePaymentFormContext())
                        .withEnvironment(environment)
                        .withTransactionId(paymentRequest.getTransactionId())
                        .withRequestContext(paymentResponseRedirect.getRequestContext())
                        .withAmount(paymentRequest.getAmount())
                        .withOrder(paymentRequest.getOrder())
                        .withBuyer(paymentRequest.getBuyer())
                        .withBrowser(paymentRequest.getBrowser())
                        .withPartnerConfiguration(paymentRequest.getPartnerConfiguration())
                        .withLocale(Locale.FRANCE)
                        .build();
        return paymentWithRedirectionService.finalizeRedirectionPayment(redirectionPaymentRequest);
    }

    private void cancelPayment(PaymentRequest request, String partnerTransactionId, String additionalData) {

        Amount amount = new Amount(BigInteger.valueOf(request.getAmount().getAmountInSmallestUnit().intValue() / 2), request.getAmount().getCurrency());
        RefundRequest refund = RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(amount)
                .withOrder(request.getOrder())
                .withBuyer(request.getBuyer())
                .withContractConfiguration(request.getContractConfiguration())
                .withEnvironment(request.getEnvironment())
                .withTransactionId(request.getTransactionId())
                .withPartnerTransactionId(partnerTransactionId)
                .withSoftDescriptor(request.getSoftDescriptor())
                .withPartnerConfiguration(request.getPartnerConfiguration())
                .withTransactionAdditionalData(additionalData)
                .build();


        RefundResponse refundResponse = refundService.refundRequest(refund);
        this.checkRefundResponseIsNotFailure(refundResponse);
        Assertions.assertEquals(RefundResponseSuccess.class, refundResponse.getClass());
        this.checkRefundResponseIsRightClass("redirectionPaymentRequest", refundResponse, RefundResponseSuccess.class);


    }


    private void checkRefundResponseIsNotFailure(RefundResponse refundResponse) {
        Assertions.assertFalse(refundResponse instanceof RefundResponseFailure, () -> {
            return "refundRequest returned RefundResponseFailure (Failure cause = " + ((RefundResponseFailure) refundResponse).getFailureCause() + ", errorCode = " + ((RefundResponseFailure) refundResponse).getErrorCode();
        });
    }

    private void checkRefundResponseIsRightClass(String requestName, RefundResponse refundResponse, Class clazz) {
        Assertions.assertTrue(refundResponse.getClass().isAssignableFrom(clazz), () -> {
            return requestName + " did not return a " + clazz.getSimpleName() + " (" + refundResponse.toString() + ")";
        });
    }
}
