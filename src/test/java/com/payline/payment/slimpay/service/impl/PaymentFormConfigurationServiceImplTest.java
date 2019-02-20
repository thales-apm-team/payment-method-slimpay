package com.payline.payment.slimpay.service.impl;

import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Locale;

import static com.payline.payment.slimpay.utils.TestUtils.*;
import static org.mockito.Mockito.when;


public class PaymentFormConfigurationServiceImplTest {


    @InjectMocks
    private PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    private final String buttonText = "Payer avec Slimpay";
    private final String decription = "Payer avec Slimpay";
    private final int height = 24;
    private final int width = 24;
    private final String paymentMethodIdentifier = "SDD Slimpay";

    private static final Environment ENVIRONMENT = new Environment("https://succesurl.com/", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);


    @Test
    public void testGetPaymentFormConfiguration() {
        //Create a form config request
        PaymentFormConfigurationRequest paymentFormConfigurationRequest = PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withLocale(Locale.FRANCE)
                .withBuyer(createDefaultBuyer())
                .withAmount(new Amount(null, Currency.getInstance("EUR")))
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(createOrder("007"))
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();

        PaymentFormConfigurationResponseSpecific paymentFormConfigurationResponse = (PaymentFormConfigurationResponseSpecific) service.getPaymentFormConfiguration(paymentFormConfigurationRequest);

        Assertions.assertNotNull(paymentFormConfigurationResponse.getPaymentForm());
        Assertions.assertEquals(buttonText, paymentFormConfigurationResponse.getPaymentForm().getButtonText());
        Assertions.assertEquals(decription, paymentFormConfigurationResponse.getPaymentForm().getDescription());
        Assertions.assertTrue(paymentFormConfigurationResponse.getPaymentForm().isDisplayButton());
    }

    @Test
    public void testGetPaymentFormLogo() {
        //Mock PaymentFormLogoRequest
        PaymentFormLogoRequest paymentFormLogoRequest = Mockito.mock(PaymentFormLogoRequest.class);
        when(paymentFormLogoRequest.getLocale()).thenReturn(Locale.FRANCE);

        PaymentFormLogoResponse paymentFormLogoResponse = service.getPaymentFormLogo(paymentFormLogoRequest);

        Assertions.assertNotNull(paymentFormLogoResponse);
        Assertions.assertTrue(paymentFormLogoResponse instanceof PaymentFormLogoResponseFile);

        PaymentFormLogoResponseFile casted = (PaymentFormLogoResponseFile) paymentFormLogoResponse;
        Assertions.assertEquals(height, casted.getHeight());
        Assertions.assertEquals(width, casted.getWidth());
    }

    @Test
    public void testGetLogo() {
        // when: getLogo is called

        PaymentFormLogo paymentFormLogo = service.getLogo(paymentMethodIdentifier, Locale.FRANCE);

        // then: returned elements are not null
        Assertions.assertNotNull(paymentFormLogo.getFile());
        Assertions.assertNotNull(paymentFormLogo.getContentType());

    }

}
