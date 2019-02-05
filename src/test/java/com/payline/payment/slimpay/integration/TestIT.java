package com.payline.payment.slimpay.integration;

import com.payline.payment.slimpay.service.impl.PaymentServiceImpl;
import com.payline.payment.slimpay.service.impl.PaymentWithRedirectionServiceImpl;
import com.payline.payment.slimpay.utils.TestUtils;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TestIT extends AbstractPaymentIntegration {
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();

    private static final String MDP_IDENTIFIER = "paymentMethodIdentifier";

    private static final Map<String, String> PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        // TODO
        put("KEY", "VALUE");
    }};

    private static final Map<String, String> SENSITIVE_PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        // TODO
        put("KEY", "VALUE");
    }};

    private static final ContractConfiguration CONTRACT_CONFIGURATION = new ContractConfiguration(MDP_IDENTIFIER, new HashMap<String, ContractProperty>() {{
        // TODO
        put("KEY", new ContractProperty("VALUE"));
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
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            // Go to partner's website
            driver.get(partnerUrl);

            // TODO
            return driver.getCurrentUrl();
            //debug

        } finally {
            driver.quit();
        }

    }

    @Override
    protected String cancelOnPartnerWebsite(String s) {
        return null;
    }

    @Test
    public void fullPaymentTest() {
        PaymentRequest request = createDefaultPaymentRequest();
        this.fullRedirectionPayment(request, paymentService, paymentWithRedirectionService);

    }


    @Override
    public PaymentRequest createDefaultPaymentRequest() {
        return TestUtils.createCompletePaymentBuilder().build();

    }
}
