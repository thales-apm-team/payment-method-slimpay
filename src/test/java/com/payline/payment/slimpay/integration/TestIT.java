package com.payline.payment.slimpay.integration;

import com.payline.payment.slimpay.service.impl.ConfigurationServiceImpl;
import com.payline.payment.slimpay.service.impl.PaymentServiceImpl;
import com.payline.payment.slimpay.service.impl.PaymentWithRedirectionServiceImpl;
import com.payline.payment.slimpay.utils.TestUtils;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.payline.payment.slimpay.utils.SlimpayConstants.*;
import static com.payline.payment.slimpay.utils.SlimpayConstants.PAYMENT_PROCESSOR;


public class TestIT extends AbstractPaymentIntegration {
    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();

    private static final String MDP_IDENTIFIER = "paymentMethodIdentifier";

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
            String code  =  driver.findElement(By.xpath("//*[@class='demo-tooltip demo-otp']//a")).getText();
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

    @Test
    public void fullPaymentTest() {
        ContractParametersCheckRequest checkRequest = TestUtils.createContractParametersCheckRequest();

        Map<String, String> checkError = configurationService.check(checkRequest);
        Assert.assertEquals(0, checkError.size());

        PaymentRequest request = createDefaultPaymentRequest();
        this.fullRedirectionPayment(request, paymentService, paymentWithRedirectionService);
    }


    @Override
    public PaymentRequest createDefaultPaymentRequest() {
        return TestUtils.createCompletePaymentBuilder().build();

    }
}
