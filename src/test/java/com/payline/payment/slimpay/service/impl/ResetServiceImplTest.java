package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import com.slimpay.hapiclient.http.JsonBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.*;
import static com.payline.payment.slimpay.utils.TestUtils.createResetRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ResetServiceImplTest {

    private static final String TRANSACTION_ID = "123456798013245";

    @InjectMocks
    ResetServiceImpl service;

    @Mock
    SlimpayHttpClient httpClient;

    private ResetRequest resetRequest;

    @BeforeEach
    public void setup() {
        service = new ResetServiceImpl();
        MockitoAnnotations.initMocks(this);

        resetRequest = createResetRequest(TRANSACTION_ID);
    }

    @Test
    public void resetRequest_nominal() throws PluginTechnicalException {
        // In the case the payment is to_process and successfully cancelled
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, true) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString() );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED) )
                .when(httpClient)
                .cancelPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString(), Mockito.any(JsonBody.class) );

        // when: calling the resetRequest method
        ResetResponse resetResponse = service.resetRequest(resetRequest);

        // then: the response is a success and its fields are not null
        assertTrue( resetResponse instanceof ResetResponseSuccess);
        ResetResponseSuccess resetSuccess = (ResetResponseSuccess) resetResponse;
        assertNotNull(resetSuccess.getStatusCode());
        assertNotNull(resetSuccess.getPartnerTransactionId());
    }

    @Test
    public void refundRequest_getPaymentFails() throws PluginTechnicalException {
        // In the case the getPayment call fails
        doReturn( createMockedSlimpayFailureResponse() )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString());

        // when: calling the resetRequest method
        ResetResponse resetResponse = service.resetRequest(resetRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( resetResponse instanceof ResetResponseFailure );
        assertValid( (ResetResponseFailure) resetResponse );
    }

    @Test
    public void refundRequest_exceptionGetPayment() throws PluginTechnicalException {
        // In the case an exception is thrown by the getPayment method
        doThrow( new PluginTechnicalException("message", "errorCode") )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString());

        // when: calling the resetRequest method
        ResetResponse resetResponse = service.resetRequest(resetRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( resetResponse instanceof ResetResponseFailure );
        assertValid( (ResetResponseFailure) resetResponse );
    }

    @Test
    public void refundRequest_paymentNotCancellable() throws PluginTechnicalException {
        // In the case the payment is not cancellable
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED, false) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString() );

        // when: calling the resetRequest method
        ResetResponse resetResponse = service.resetRequest(resetRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( resetResponse instanceof ResetResponseFailure );
        assertValid( (ResetResponseFailure) resetResponse );
    }

    @Test
    public void refundRequest_cancelPaymentFails() throws PluginTechnicalException {
        // In the case the payment is to_process, but the cancellation fails
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, true) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString() );
        doReturn( createMockedCancelPaymentError() )
                .when(httpClient)
                .cancelPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString(), Mockito.any(JsonBody.class) );

        // when: calling the resetRequest method
        ResetResponse resetResponse = service.resetRequest(resetRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( resetResponse instanceof ResetResponseFailure );
        assertValid( (ResetResponseFailure) resetResponse );
    }

    @Test
    public void refundRequest_exceptionCancelPayment() throws PluginTechnicalException {
        // In the case the payment is to_process, but the cancellation throws an exception
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, true) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString() );
        doReturn( createMockedCancelPaymentError() )
                .when(httpClient)
                .cancelPayment( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString(), Mockito.any(JsonBody.class) );

        // when: calling the resetRequest method
        ResetResponse resetResponse = service.resetRequest(resetRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( resetResponse instanceof ResetResponseFailure );
        assertValid( (ResetResponseFailure) resetResponse );
    }

    private static void assertValid( ResetResponseFailure resetResponseFailure ){
        assertNotNull(resetResponseFailure.getErrorCode());
        assertNotNull(resetResponseFailure.getFailureCause());
        assertNotNull(resetResponseFailure.getPartnerTransactionId());
    }

}
