package com.payline.payment.slimpay.business.impl;

import com.payline.pmapi.bean.buyer.request.BuyerDetailsRequest;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestConfigBusinessImplTest {

    private static final String PARTNER = "PARTNER";
    private static final String CONTRACT = "CONTRACT";
    private final String CONTRACT_CONF = "CONTRACT_CONF";
    private final String PARTNER_CONF = "PARTNER_CONF";
    private final String EXT = "EXT";

    private PartnerConfiguration partnerConfiguration;
    private ContractConfiguration contractConfiguration;
    private Map<String, String> accountInfo;
    private Map<String, RequestConfigBusinessImpl.PaylineParameterType> copy = new HashMap<>(RequestConfigBusinessImpl.PARAMETERS_MAP);

    private RequestConfigBusinessImpl service = RequestConfigBusinessImpl.getInstance();

    @BeforeAll
    public void setup() {
        Map<String, RequestConfigBusinessImpl.PaylineParameterType> map = new HashMap<>();

        map.put(CONTRACT_CONF, RequestConfigBusinessImpl.PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        map.put(EXT, RequestConfigBusinessImpl.PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        map.put(PARTNER_CONF, RequestConfigBusinessImpl.PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);

        Whitebox.setInternalState(RequestConfigBusinessImpl.class, "PARAMETERS_MAP", map);

        partnerConfiguration = new PartnerConfiguration(new HashMap<>(), new HashMap<>());
        partnerConfiguration.getSensitiveProperties().put(PARTNER_CONF, PARTNER);

        contractConfiguration = new ContractConfiguration("test", new HashMap<>());
        contractConfiguration.getContractProperties().put(CONTRACT_CONF, new ContractProperty(CONTRACT));
        contractConfiguration.getContractProperties().put(EXT, new ContractProperty(EXT));

        accountInfo = new HashMap<>();
        accountInfo.put(CONTRACT_CONF, "CONTRACT");
        accountInfo.put(EXT, EXT);
    }

    @AfterAll
    public void tearDown() {
        // réinit de la map ...
        Whitebox.setInternalState(RequestConfigBusinessImpl.class, "PARAMETERS_MAP", copy);
    }

    @Test
    void getParameterValue_ResetRequest() {
        ResetRequest request = Mockito.mock(ResetRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_RefundRequest() {
        RefundRequest request = Mockito.mock(RefundRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_PaymentFormLogoRequest() {
        PaymentFormLogoRequest request = Mockito.mock(PaymentFormLogoRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_PaymentFormConfigurationRequest() {
        PaymentFormConfigurationRequest request = Mockito.mock(PaymentFormConfigurationRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_NotifyTransactionStatusRequest() {
        NotifyTransactionStatusRequest request = Mockito.mock(NotifyTransactionStatusRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_PaymentRequest() {
        PaymentRequest request = Mockito.mock(PaymentRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_TransactionStatusRequest() {
        TransactionStatusRequest request = Mockito.mock(TransactionStatusRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_ContractParametersCheckRequest() {
        ContractParametersCheckRequest request = Mockito.mock(ContractParametersCheckRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);
        Mockito.when(request.getAccountInfo()).thenReturn(accountInfo);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_CaptureRequest() {
        CaptureRequest request = Mockito.mock(CaptureRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void getParameterValue_BuyerDetailsRequest() {
        BuyerDetailsRequest request = Mockito.mock(BuyerDetailsRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        String nullKey = service.getParameterValue(request, null);
        Assertions.assertNull(nullKey);

        String contractConf = service.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(contractConf);
        Assertions.assertEquals(CONTRACT, contractConf);

        String partnerConf = service.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(partnerConf);
        Assertions.assertEquals(PARTNER, partnerConf);
    }

    @Test
    void safeGetValuePartnerConfigurationAndKey() {
        String nullSource = service.safeGetValue((PartnerConfiguration) null, "test");
        Assertions.assertNull(nullSource);

        String nullKey = service.safeGetValue(partnerConfiguration, null);
        Assertions.assertNull(nullKey);

        String emptyKey = service.safeGetValue(partnerConfiguration, "");
        Assertions.assertNull(emptyKey);

        String wrongKey = service.safeGetValue(partnerConfiguration, "wrong");
        Assertions.assertNull(wrongKey);
    }

    @Test
    void safeGetValueContractConfigurationAndKey() {
        String nullSource = service.safeGetValue((ContractConfiguration) null, "test");
        Assertions.assertNull(nullSource);

        String nullKey = service.safeGetValue(contractConfiguration, null);
        Assertions.assertNull(nullKey);

        String emptyKey = service.safeGetValue(contractConfiguration, "");
        Assertions.assertNull(emptyKey);

        String wrongKey = service.safeGetValue(contractConfiguration, "wrong");
        Assertions.assertNull(wrongKey);
    }

    @Test
    void safeGetValueAccountInfoAndKey() {
        String nullSource = service.safeGetValue((HashMap) null, "test");
        Assertions.assertNull(nullSource);

        String nullKey = service.safeGetValue(accountInfo, null);
        Assertions.assertNull(nullKey);

        String emptyKey = service.safeGetValue(accountInfo, "");
        Assertions.assertNull(emptyKey);

        String wrongKey = service.safeGetValue(accountInfo, "wrong");
        Assertions.assertNull(wrongKey);
    }

}