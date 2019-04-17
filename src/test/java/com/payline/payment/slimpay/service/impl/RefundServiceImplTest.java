package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.slimpay.hapiclient.http.JsonBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.*;
import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundServiceImplTest {

    private static final String TRANSACTION_ID = "123456798013245";

    @InjectMocks
    RefundServiceImpl service;

    @Mock
    SlimpayHttpClient httpClient;

    private RefundRequest refundRequest;

    @BeforeEach
    public void setup() {
        service = new RefundServiceImpl();
        MockitoAnnotations.initMocks(this);

        refundRequest = createRefundRequest(TRANSACTION_ID, "100");
    }

    @Test
    public void refundRequest_nominal() throws PluginTechnicalException {
        // In the case the payment is processed and the payout is successfully created
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), anyString() );
        doReturn( createMockedSlimpayPaymentOut(PaymentExecutionStatus.TO_PROCESS) )
                .when(httpClient)
                .createPayout( any(PartnerConfiguration.class), Mockito.any(JsonBody.class) );

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a success and its fields are not null
        assertTrue( refundResponse instanceof RefundResponseSuccess );
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        assertNotNull(refundSuccess.getStatusCode());
        assertNotNull(refundSuccess.getPartnerTransactionId());
    }

    @Test
    public void refundRequest_getPaymentFails() throws PluginTechnicalException {
        // In the case the getPayment call fails
        doReturn( createMockedSlimpayFailureResponse() )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValid( (RefundResponseFailure) refundResponse );
    }

    @Test
    public void refundRequest_exceptionGetPayment() throws PluginTechnicalException {
        // In the case an exception is thrown by the getPayment method
        doThrow( new PluginTechnicalException("message", "errorCode") )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the service handle the exception. So the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValid( (RefundResponseFailure) refundResponse );
    }

    @Test
    public void refundRequest_paymentNotProcessed() throws PluginTechnicalException {
        // In the case the payment has not been processed
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), anyString() );

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValid( (RefundResponseFailure) refundResponse );
    }

    @Test
    public void refundRequest_payoutCreationFails() throws PluginTechnicalException {
        // In the case the payout creation fails
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED) )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());
        doReturn( createMockedSlimpayPaymentOutError() )
                .when(httpClient)
                .createPayout(any(PartnerConfiguration.class), any(JsonBody.class));

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValid( (RefundResponseFailure) refundResponse );
    }

    @Test
    public void refundRequest_exceptionCreatePayout() throws PluginTechnicalException {
        // In the case an exception is thrown by the createPayout method
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED) )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());
        doThrow( new PluginTechnicalException("message", "errorCode") )
                .when(httpClient)
                .createPayout(any(PartnerConfiguration.class), any(JsonBody.class));

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the service handle the exception. So the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValid( (RefundResponseFailure) refundResponse );
    }

    private static void assertValid( RefundResponseFailure refundResponseFailure ){
        assertNotNull(refundResponseFailure.getErrorCode());
        assertNotNull(refundResponseFailure.getFailureCause());
        assertNotNull(refundResponseFailure.getPartnerTransactionId());
    }

}
