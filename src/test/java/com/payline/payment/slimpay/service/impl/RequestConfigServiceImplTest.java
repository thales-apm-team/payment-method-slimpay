package com.payline.payment.slimpay.service.impl;

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
class RequestConfigServiceImplTest {

    protected static final String PARTNER = "PARTNER";
    protected static final String CONTRACT = "CONTRACT";
    private final String CONTRACT_CONF = "CONTRACT_CONF";
    private final String PARTNER_CONF = "PARTNER_CONF";


    private PartnerConfiguration partnerConfiguration;

    private ContractConfiguration contractConfiguration;

    private Map<String, String> accountInfo;

    private final String EXT = "EXT";

    private String parametervalue;

    private Map<String, RequestConfigServiceImpl.PaylineParameterType> copy = new HashMap<>(RequestConfigServiceImpl.INSTANCE.PARAMETERS_MAP);

    @BeforeAll
    public void setup() {


        Map<String, RequestConfigServiceImpl.PaylineParameterType> map = new HashMap<>();

        map.put(CONTRACT_CONF, RequestConfigServiceImpl.PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        map.put(EXT, RequestConfigServiceImpl.PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        map.put(PARTNER_CONF, RequestConfigServiceImpl.PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);

        Whitebox.setInternalState(RequestConfigServiceImpl.class, "PARAMETERS_MAP", map);


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
        Whitebox.setInternalState(RequestConfigServiceImpl.class, "PARAMETERS_MAP", copy);
    }

    @Test
    void getParameterValue_ResetRequest() throws Exception {

        ResetRequest request = Mockito.mock(ResetRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_RefundRequest() throws Exception {

        RefundRequest request = Mockito.mock(RefundRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_PaymentFormLogoRequest() throws Exception {

        PaymentFormLogoRequest request = Mockito.mock(PaymentFormLogoRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_PaymentFormConfigurationRequest() throws Exception {

        PaymentFormConfigurationRequest request = Mockito.mock(PaymentFormConfigurationRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_NotifyTransactionStatusRequest() throws Exception {

        NotifyTransactionStatusRequest request = Mockito.mock(NotifyTransactionStatusRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_PaymentRequest() throws Exception {

        PaymentRequest request = Mockito.mock(PaymentRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_TransactionStatusRequest() throws Exception {

        TransactionStatusRequest request = Mockito.mock(TransactionStatusRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_ContractParametersCheckRequest() throws Exception {

        ContractParametersCheckRequest request = Mockito.mock(ContractParametersCheckRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);
        Mockito.when(request.getAccountInfo()).thenReturn(accountInfo);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_CaptureRequest() throws Exception {

        CaptureRequest request = Mockito.mock(CaptureRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

    @Test
    void getParameterValue_BuyerDetailsRequest() throws Exception {

        BuyerDetailsRequest request = Mockito.mock(BuyerDetailsRequest.class);

        Mockito.when(request.getContractConfiguration()).thenReturn(contractConfiguration);
        Mockito.when(request.getPartnerConfiguration()).thenReturn(partnerConfiguration);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, null);
        Assertions.assertNull(parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, CONTRACT_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(CONTRACT, parametervalue);

        parametervalue = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, PARTNER_CONF);
        Assertions.assertNotNull(parametervalue);
        Assertions.assertEquals(PARTNER, parametervalue);

    }

}