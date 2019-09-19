package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.exception.MalformedResponseException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.DateUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import com.slimpay.hapiclient.http.JsonBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    private Calendar calendar = GregorianCalendar.getInstance();

    @BeforeEach
    public void setup() {
        service = new RefundServiceImpl();
        MockitoAnnotations.initMocks(this);

        refundRequest = createRefundRequest(TRANSACTION_ID, "100");
    }

    @Test
    void refundRequest_getPaymentException() throws PluginTechnicalException {
        // In the case an exception is thrown by the getPayment method
        doThrow( new PluginTechnicalException("message", "errorCode") )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the service handle the exception. So the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_getPaymentFails() throws PluginTechnicalException {
        // In the case the getPayment call fails
        doReturn( createMockedSlimpayFailureResponse() )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_reset_notCancellable() throws PluginTechnicalException {
        // In the case the payment is not yet executed but not cancellable
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, 1 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, calendar.getTime(), false) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), anyString() );

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_reset_cancelError() throws PluginTechnicalException {
        // In the case the payment is not yet processed and cancellable, but the cancellation fails
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, 3 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, calendar.getTime(), true) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), anyString() );
        doReturn( createMockedCancelPaymentError() )
                .when(httpClient)
                .cancelPayment( any(PartnerConfiguration.class), anyString(), Mockito.any(JsonBody.class) );

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_reset_cancelFails() throws PluginTechnicalException {
        // In the case the payment seems to be cancelled, but the returned payment is still TO_PROCESS
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, 3 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, calendar.getTime(), true) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), anyString() );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS) )
                .when(httpClient)
                .cancelPayment( any(PartnerConfiguration.class), anyString(), Mockito.any(JsonBody.class) );

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_reset_cancelSuccess() throws PluginTechnicalException {
        // In the case the payment is successfully cancelled
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, 3 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS, calendar.getTime(), true) )
                .when(httpClient)
                .getPayment( any(PartnerConfiguration.class), anyString() );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED) )
                .when(httpClient)
                .cancelPayment( any(PartnerConfiguration.class), anyString(), Mockito.any(JsonBody.class) );

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a success and its fields are not null
        assertTrue( refundResponse instanceof RefundResponseSuccess);
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        assertNotNull(refundSuccess.getStatusCode());
        assertNotNull(refundSuccess.getPartnerTransactionId());
    }

    @Test
    void refundRequest_refund_tooSoon() throws PluginTechnicalException {
        // In the case the execution date is too close
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, -2 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED, calendar.getTime(), false) )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_refund_createPayoutFails() throws PluginTechnicalException {
        // In the case the payout creation fails
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, -12 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED, calendar.getTime(), false) )
                .when(httpClient)
                .getPayment(any(PartnerConfiguration.class), anyString());
        doReturn( createMockedSlimpayPaymentOutError() )
                .when(httpClient)
                .createPayout(any(PartnerConfiguration.class), any(JsonBody.class));

        // when: calling the refundRequest method
        RefundResponse refundResponse = service.refundRequest(refundRequest);

        // then: the response is a valid RefundResponseFailure.
        assertTrue( refundResponse instanceof RefundResponseFailure );
        assertValidFailure( (RefundResponseFailure) refundResponse );
    }

    @Test
    void refundRequest_refund_createPayoutSuccess() throws PluginTechnicalException {
        // In the case the payment is processed and the payout is successfully created
        calendar.setTime( new Date() );
        calendar.add( Calendar.DATE, -12 );
        doReturn( createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED, calendar.getTime(), false) )
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

    private static void assertValidFailure( RefundResponseFailure refundResponseFailure ){
        assertNotNull(refundResponseFailure.getErrorCode());
        assertNotNull(refundResponseFailure.getFailureCause());
        assertNotNull(refundResponseFailure.getPartnerTransactionId());
    }

}
