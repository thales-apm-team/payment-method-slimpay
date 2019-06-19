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
import org.junit.jupiter.api.Assertions;
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


public class TestIT extends AbstractPaymentIntegration {
    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();

    @Override
    protected Map<String, ContractProperty> generateParameterContract() {
        return TestUtils.CONTRACT_CONFIGURATION.getContractProperties();
    }

    @Override
    protected PaymentFormContext generatePaymentFormContext() {

        return PaymentFormContext.PaymentFormContextBuilder
                .aPaymentFormContext()
                .withPaymentFormParameter(TestUtils.PARTNER_CONFIGURATION_MAP)
                .withSensitivePaymentFormParameter(TestUtils.SENSITIVE_PARTNER_CONFIGURATION_MAP)
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

    @Test
    public void fullPaymentTest() {
        PaymentRequest request = createDefaultPaymentRequest();
        this.fullRedirectionPayment(request, paymentService, paymentWithRedirectionService);
    }

    @Test
    public void configurationServiceCheckTest() {
        //test method check() of ConfigurationService
        ContractParametersCheckRequest checkRequest = TestUtils.createContractParametersCheckRequest();
        Map<String, String> checkError = configurationService.check(checkRequest);
        Assertions.assertEquals(0, checkError.size());

    }


    @Override
    public PaymentRequest createDefaultPaymentRequest() {
        return TestUtils.createCompletePaymentBuilder().build();
    }
}
