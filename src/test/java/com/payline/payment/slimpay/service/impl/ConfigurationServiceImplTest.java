package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigurationServiceImplTest {

    @InjectMocks
    private ConfigurationServiceImpl service;

    @Spy
    SlimpayHttpClient httpClient;

    private Map<String, String> accountInfo = new HashMap<>();

    private Environment environment;

    @BeforeAll
    public void setUp() throws Exception {
        service = new ConfigurationServiceImpl();
        MockitoAnnotations.initMocks(this);

        environment = new Environment("https://succesurl.com/", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);

    }

    @Test
    public void testGetParameters() {

        List<AbstractParameter> parameters = service.getParameters(Locale.FRANCE);
        //Assert we have 3 parameters
        Assertions.assertNotNull(parameters);

        // TODO
        Assertions.assertEquals(0, parameters.size());

        List<String> result = new ArrayList<>();
        for (AbstractParameter paramter : parameters) {
            result.add(paramter.getKey());
        }

    }

    @Test
    public void checkOK() {

        final ContractConfiguration contractConfiguration = new ContractConfiguration("Oney", new HashMap<>());
        // TODO

        Map<String, String> partnerConfiguration = new HashMap<>();
        // TODO

        Map<String, String> sensitivePartnerConfiguration = new HashMap<>();
        // TODO

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
    public void checkKO() {

        final ContractConfiguration contractConfiguration = new ContractConfiguration("Oney", new HashMap<>());
        // TODO

        Map<String, String> partnerConfiguration = new HashMap<>();
        // TODO

        Map<String, String> sensitivePartnerConfiguration = new HashMap<>();
        // TODO

        ContractParametersCheckRequest contractParametersCheckRequest = ContractParametersCheckRequest.CheckRequestBuilder
                .aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(Locale.FRANCE)
                .withPartnerConfiguration(new PartnerConfiguration(partnerConfiguration, sensitivePartnerConfiguration))
                .withContractConfiguration(contractConfiguration)
                .withEnvironment(environment)
                .build();

        Map<String, String> errors = service.check(contractParametersCheckRequest);
        Assertions.assertEquals(1, errors.size());
    }

}
