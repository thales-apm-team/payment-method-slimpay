package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.*;
import java.util.regex.Pattern;

import static com.payline.payment.slimpay.utils.SlimpayConstants.*;


//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigurationServiceImplTest {

    @InjectMocks
    private static ConfigurationServiceImpl service;

    @Spy
    SlimpayHttpClient httpClient;

    private Map<String, String> accountInfo = new HashMap<>();

    private static Environment environment;

    @BeforeAll
    public static void setUp() throws Exception {
        service = new ConfigurationServiceImpl();
        MockitoAnnotations.initMocks(ConfigurationServiceImplTest.class);

        environment = new Environment("https://succesurl.com/", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);

    }

    @Test
    public void testGetParameters() {

        List<AbstractParameter> parameters = service.getParameters(Locale.FRANCE);
        //Assert we have 5 parameters
        Assertions.assertNotNull(parameters);

        Assertions.assertEquals(5, parameters.size());
        List<String> result = new ArrayList<>();
        for (AbstractParameter paramter : parameters) {
            result.add(paramter.getKey());
        }

        Assertions.assertTrue(result.contains(SlimpayConstants.CREDITOR_REFERENCE_KEY));
        Assertions.assertTrue(result.contains(SlimpayConstants.MANDATE_PAYIN_SCHEME));
        Assertions.assertTrue(result.contains(SlimpayConstants.PAYMENT_PROCESSOR));
        Assertions.assertTrue(result.contains(SlimpayConstants.FIRST_PAYMENT_SCHEME));
        Assertions.assertTrue(result.contains(SlimpayConstants.SIGNATURE_APPROVAL_METHOD));
    }

    @Test
    public void testGetParametersKeys() {
        List<AbstractParameter> parameters = service.getParameters(Locale.FRANCE);
        Pattern p = Pattern.compile("[a-zA-Z\\.]*");
        for (AbstractParameter param : parameters) {
            Assertions.assertTrue(p.matcher(param.getKey()).matches(), param.getKey() + " comporte des caracteres interdits");
        }
    }

    @Test
    public void checkOK() {
        final ContractConfiguration contractConfiguration = new ContractConfiguration("Oney", new HashMap<String, ContractProperty>() {
            {
                put(CREDITOR_REFERENCE_KEY, new ContractProperty("paylinemerchanttest1"));
                put(FIRST_PAYMENT_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
                put(MANDATE_PAYIN_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
                put(MANDATE_STANDARD_KEY, new ContractProperty("SEPA"));
                put(SIGNATURE_APPROVAL_METHOD, new ContractProperty("otp"));
                put(PAYMENT_PROCESSOR, new ContractProperty("slimpay"));
            }
        });

        Map<String, String> partnerConfiguration = new HashMap<String, String>() {{
            put(API_URL_KEY, "https://api.preprod.slimpay.com");
            put(API_PROFILE_KEY, "https://api.slimpay.net/alps/v1");
            put(API_NS_KEY, "https://api.slimpay.net/alps");
            put(APP_KEY, "monextreferral01");
        }};

        Map<String, String> sensitivePartnerConfiguration = new HashMap<String, String>(){{
            put(APP_SECRET, "n32cXdaS0ZOACV8688ltKovAO6lquL4wKjZHnvyO");
        }};

        ContractParametersCheckRequest contractParametersCheckRequest = ContractParametersCheckRequest.CheckRequestBuilder
                .aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(Locale.FRANCE)
                .withPartnerConfiguration(new PartnerConfiguration(partnerConfiguration, sensitivePartnerConfiguration))
                .withContractConfiguration(contractConfiguration)
                .withEnvironment(environment)
                .build();

        Map<String, String> errors = service.check(contractParametersCheckRequest);
        Assertions.assertEquals(0, errors.size());

    }

    @Test
    public void checkKOEmptyParameters() {
        final ContractConfiguration contractConfiguration = new ContractConfiguration("Oney", new HashMap<>());
        Map<String, String> partnerConfiguration = new HashMap<>();
        Map<String, String> sensitivePartnerConfiguration = new HashMap<>();

        ContractParametersCheckRequest contractParametersCheckRequest = ContractParametersCheckRequest.CheckRequestBuilder
                .aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(Locale.FRANCE)
                .withPartnerConfiguration(new PartnerConfiguration(partnerConfiguration, sensitivePartnerConfiguration))
                .withContractConfiguration(contractConfiguration)
                .withEnvironment(environment)
                .build();

        Map<String, String> errors = service.check(contractParametersCheckRequest);
        Assertions.assertEquals(7, errors.size());
        Assertions.assertTrue(errors.containsKey(CREDITOR_REFERENCE_KEY));
        Assertions.assertTrue(errors.containsKey(FIRST_PAYMENT_SCHEME));
        Assertions.assertTrue(errors.containsKey(MANDATE_PAYIN_SCHEME));
        Assertions.assertTrue(errors.containsKey(SIGNATURE_APPROVAL_METHOD));
        Assertions.assertTrue(errors.containsKey(PAYMENT_PROCESSOR));
        Assertions.assertTrue(errors.containsKey(APP_KEY));
        Assertions.assertTrue(errors.containsKey(APP_SECRET));

    }

    @Test
    public void checkKOConnectionFails(){

        final ContractConfiguration contractConfiguration = new ContractConfiguration("Oney", new HashMap<String, ContractProperty>() {
            {
                put(CREDITOR_REFERENCE_KEY, new ContractProperty("paylinemerchanttest1"));
                put(FIRST_PAYMENT_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
                put(MANDATE_PAYIN_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
                put(MANDATE_STANDARD_KEY, new ContractProperty("SEPA"));
                put(SIGNATURE_APPROVAL_METHOD, new ContractProperty("otp"));
                put(PAYMENT_PROCESSOR, new ContractProperty("slimpay"));
            }
        });

        Map<String, String> partnerConfiguration = new HashMap<String, String>() {{
            put(API_URL_KEY, "https://api.preprod.slimpay.com");
            put(API_PROFILE_KEY, "https://api.slimpay.net/alps/v1");
            put(API_NS_KEY, "https://api.slimpay.net/alps");
            put(APP_KEY, "Zmonextreferral01Z");
        }};

        Map<String, String> sensitivePartnerConfiguration = new HashMap<String, String>(){{
            put(APP_SECRET, "n32cXdaS0ZOACV8688ltKovAO6lquL4wKjZHnvyO");
        }};

        ContractParametersCheckRequest contractParametersCheckRequest = ContractParametersCheckRequest.CheckRequestBuilder
                .aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(Locale.FRANCE)
                .withPartnerConfiguration(new PartnerConfiguration(partnerConfiguration, sensitivePartnerConfiguration))
                .withContractConfiguration(contractConfiguration)
                .withEnvironment(environment)
                .build();
//todo  mock http call

//        HttpClientErrorException errorMocked = new HttpClientErrorException(null, null, "{  " +
//                "   \"timestamp\" : \"2019-02-14T13:58:52.977+0000\",\n" +
//                "                \"status\" : 401,\n" +
//                "                \"error\" : \"Unauthorized\",\n" +
//                "                \"message\" : \"Bad credentials\",\n" +
//                "                \"path\" : \"/oauth/token\"   }");

                Map<String, String> errors = service.check(contractParametersCheckRequest);
        Assertions.assertEquals(1, errors.size());

    }

}
