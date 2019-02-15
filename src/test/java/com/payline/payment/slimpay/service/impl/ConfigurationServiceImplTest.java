package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.BeansUtils;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.payment.slimpay.utils.TestUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import com.slimpay.hapiclient.exception.HttpException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.regex.Pattern;

import static com.payline.payment.slimpay.utils.SlimpayConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfigurationServiceImplTest {

    @InjectMocks
    private static ConfigurationServiceImpl service;

    @Mock
    private SlimpayHttpClient httpClient;

    private Map<String, String> accountInfo = new HashMap<>();

    private static Environment environment = TestUtils.ENVIRONMENT;;

    @BeforeAll
    public static void setUp() {
        service = new ConfigurationServiceImpl();
        MockitoAnnotations.initMocks(ConfigurationServiceImplTest.class);
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
    public void checkOK() throws PluginTechnicalException, HttpException {
        when(httpClient.testConnection(any(), any())).thenReturn(BeansUtils.createMockedSlimpayOrderResponseOpen());
        ContractParametersCheckRequest contractParametersCheckRequest = TestUtils.createContractParametersCheckRequest();

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
    public void checkKOConnectionFails() throws PluginTechnicalException, HttpException {
        when(httpClient.testConnection(any(), any())).thenThrow(new HttpCallException("401", "bar"));
        ContractParametersCheckRequest contractParametersCheckRequest = TestUtils.createContractParametersCheckRequest();

        Map<String, String> errors = service.check(contractParametersCheckRequest);
        Assertions.assertEquals(1, errors.size());

    }

    @Test
    public void testGetReleaseInformation_versionFormat(){
        // when: getReleaseInformation method is called
        ReleaseInformation releaseInformation = service.getReleaseInformation();

        // then: the version has a valid format
        Assertions.assertNotNull( releaseInformation );
        Assertions.assertTrue( releaseInformation.getVersion().matches( "^\\d\\.\\d(\\.\\d)?$" ) );
    }

    @Test
    public void getName() {
        String name = service.getName( Locale.FRENCH);
        Assertions.assertNotNull(name);
        Assertions.assertNotEquals(0, name.length());
    }

}

