package com.payline.payment.slimpay.service;

import com.payline.payment.slimpay.service.impl.RequestConfigServiceImpl;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestConfigServiceTest {

    private RequestConfigService service = RequestConfigServiceImpl.INSTANCE;

    private PartnerConfiguration partnerConfiguration;

    private ContractConfiguration contractConfiguration;

    private Map<String, String> accountInfo;

    private String key = "KEY";
    private String safevalue;


    @BeforeAll
    public void setup() {
        partnerConfiguration = new PartnerConfiguration(new HashMap<>(), new HashMap<>());
        partnerConfiguration.getSensitiveProperties().put(key, "test");

        contractConfiguration = new ContractConfiguration("test", new HashMap<>());
        contractConfiguration.getContractProperties().put(key, new ContractProperty("test"));

        accountInfo = new HashMap<>();
        accountInfo.put(key, "test");
    }

    @Test
    void safeGetValuePartnerConfigurationAndKey() {

        safevalue = service.safeGetValue((PartnerConfiguration) null, "test");
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(partnerConfiguration, null);
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(partnerConfiguration, "");
        Assertions.assertNull(safevalue);


        safevalue = service.safeGetValue(partnerConfiguration, key);
        Assertions.assertEquals("test", safevalue);
    }

    @Test
    void safeGetValueContractConfigurationAndKey() {

        safevalue = service.safeGetValue((ContractConfiguration) null, "test");
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(contractConfiguration, null);
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(contractConfiguration, "zz");
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(contractConfiguration, key);
        Assertions.assertEquals("test", safevalue);
    }

    @Test
    void safeGetValueAccountInfoAndKey() {

        safevalue = service.safeGetValue((HashMap) null, "test");
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(accountInfo, "");
        Assertions.assertNull(safevalue);

        safevalue = service.safeGetValue(accountInfo, null);
        Assertions.assertNull(safevalue);


        safevalue = service.safeGetValue(accountInfo, key);
        Assertions.assertEquals("test", safevalue);
    }
}