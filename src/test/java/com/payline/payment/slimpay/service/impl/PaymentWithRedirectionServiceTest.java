package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.BeansUtils;
import com.payline.payment.slimpay.utils.TestUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.OrderStatus;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Currency;
import java.util.stream.Stream;

import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayOrderResponse;
import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayPaymentIn;
import static com.payline.payment.slimpay.utils.TestUtils.createDefaultTransactionStatusRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createRedirectionPaymentRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentWithRedirectionServiceTest {

    private static final String TRANSACTION_ID = "HDEV-1500000000000";

    @InjectMocks
    private PaymentWithRedirectionServiceImpl service;

    @Mock
    private SlimpayHttpClient httpClient;

    @BeforeEach
    public void setup() {
        service = new PaymentWithRedirectionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * In the nominal case of finalizeRedirectionPayment(), the order status is 'closed.completed'
     * and the payment status is 'toprocess'.
     * @throws PluginTechnicalException
     */
    @Test
    public void finalizeRedirectionPayment_nominal() throws PluginTechnicalException {
        // Mock order state
        Mockito.doReturn(createMockedSlimpayOrderResponse(OrderStatus.CLOSED_COMPLETED))
                .when(httpClient)
                .getOrder(any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString());
        // Mock payment state
        Mockito.doReturn(createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS))
                .when(httpClient)
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );

        // when: calling method finalizeRedirectionPayment()
        PaymentResponse response = service.finalizeRedirectionPayment(createRedirectionPaymentRequest(TRANSACTION_ID));

        // then: response is a success
        assertTrue(response instanceof PaymentResponseSuccess);
        // TODO: change it if payment with status 'toprocess' must return a PaymentResponseOnHold (see PAYLAPMEXT-124)
    }

    /**
     * In the nominal case of handleSessionExpired(), the user session has expired while he was filling the form on the
     * partner's web site. So the order status should be 'open.running', because the mandate is probably not signed.
     * @throws PluginTechnicalException
     */
    @Test
    public void handleSessionExpired_nominal() throws PluginTechnicalException {
        // Mock order state
        Mockito.doReturn(createMockedSlimpayOrderResponse(OrderStatus.OPEN_RUNNING))
                .when(httpClient)
                .getOrder(any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString());

        // when: calling the method handleSessionExpired()
        PaymentResponse response = service.handleSessionExpired(createDefaultTransactionStatusRequest(TRANSACTION_ID));

        // then: response is "on hold"
        assertTrue(response instanceof PaymentResponseOnHold);
    }

    /**
     * Test set build upon the documentation :
     * https://payline.atlassian.net/wiki/spaces/APMSDK/pages/1192919570/Slimpay+-+Analyse#Slimpay-Analyse-%C3%89tatspossiblesd'unordre'
     */
    private static Stream<Arguments> checkOrderTestSet() {
        return Stream.of(
                Arguments.of(OrderStatus.OPEN, PaymentResponseOnHold.class, null),
                Arguments.of(OrderStatus.OPEN_RUNNING, PaymentResponseOnHold.class, null),
                Arguments.of(OrderStatus.OPEN_NOT_RUNNING, PaymentResponseOnHold.class, null),
                Arguments.of(OrderStatus.CLOSED_ABORTED, PaymentResponseFailure.class, FailureCause.REFUSED),
                Arguments.of(OrderStatus.CLOSED_ABORTED_BY_CLIENT, PaymentResponseFailure.class, FailureCause.CANCEL),
                Arguments.of(OrderStatus.CLOSED_ABORTED_BY_SERVER, PaymentResponseFailure.class, FailureCause.REFUSED),
                Arguments.of(OrderStatus.CLOSED_COMPLETED, PaymentResponseSuccess.class, null)
        );
    }

    @ParameterizedTest
    @MethodSource("checkOrderTestSet")
    public void checkOrder( String inputOrderStatus, Class expectedResponseClass, FailureCause expectedFailureCause ) throws PluginTechnicalException {
        // Mock order state
        Mockito.doReturn(createMockedSlimpayOrderResponse( inputOrderStatus ))
                .when( httpClient )
                .getOrder(any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString());
        // Mock payment state (in case it's needed) : 'toprocess', as it is at the end of the process
        Mockito.doReturn(createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS))
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );

        // when: calling method checkOrder()
        PaymentResponse response = service.checkOrder(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), TestUtils.createDefaultOrder() );

        // then: response is as expected
        assertTrue( expectedResponseClass.isInstance( response ) );
        if( expectedResponseClass == PaymentResponseFailure.class ){
            assertEquals( expectedFailureCause, ((PaymentResponseFailure)response).getFailureCause() );
            assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        }
    }

    @Test
    public void checkOrder_exception() throws PluginTechnicalException {
        // Mock exception during getOrder()
        Mockito.doThrow( new HttpCallException("Exception message longer than 50 characters (which is the maximum allowed", "SlimpayHttpClient.getOrder") )
                .when( httpClient )
                .getOrder( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString() );

        // when: calling method checkOrder()
        PaymentResponse response = service.checkOrder(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), TestUtils.createDefaultOrder() );

        // then: response is a failure, with valid failure cause and error code
        assertTrue( response instanceof PaymentResponseFailure );
        assertNotNull( ((PaymentResponseFailure)response).getFailureCause() );
        assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        assertTrue( ((PaymentResponseFailure)response).getErrorCode().length() <= 50 );
    }

    @Test
    public void checkOrder_partnerError() throws PluginTechnicalException {
        // Mock order state: partner error
        Mockito.doReturn( BeansUtils.createMockedSlimpayFailureResponse() )
                .when( httpClient )
                .getOrder( any(PartnerConfiguration.class), any(ContractConfiguration.class), anyString() );

        // when: calling method checkOrder()
        PaymentResponse response = service.checkOrder(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), TestUtils.createDefaultOrder() );

        // then: response is a failure, with valid failure cause and error code
        assertTrue( response instanceof PaymentResponseFailure );
        assertNotNull( ((PaymentResponseFailure)response).getFailureCause() );
        assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        assertTrue( ((PaymentResponseFailure)response).getErrorCode().length() <= 50 );
    }

    /**
     * Test set build upon the documentation :
     * https://payline.atlassian.net/wiki/spaces/APMSDK/pages/1192919570/Slimpay+-+Analyse#Slimpay-Analyse-Descriptiondesdiff%C3%A9rents%C3%A9tatsd'unpaiement(etremboursement)
     */
    private static Stream<Arguments> checkPaymentTestSet() {
        return Stream.of(
                Arguments.of(PaymentExecutionStatus.TO_PROCESS, PaymentResponseSuccess.class, null), // TODO: OnHold ? (see PAYLAPMEXT-124)
                Arguments.of(PaymentExecutionStatus.PROCESSING, PaymentResponseSuccess.class, null),
                Arguments.of(PaymentExecutionStatus.NOT_PROCESSED, PaymentResponseFailure.class, FailureCause.REFUSED),
                Arguments.of(PaymentExecutionStatus.TO_REPLAY, PaymentResponseSuccess.class, null), // TODO: OnHold ? (see PAYLAPMEXT-124)
                Arguments.of(PaymentExecutionStatus.PROCESSED, PaymentResponseSuccess.class, null),
                Arguments.of(PaymentExecutionStatus.REJECTED, PaymentResponseFailure.class, FailureCause.REFUSED) // mocked reason code AC04 => REFUSED
        );
    }

    @ParameterizedTest
    @MethodSource("checkPaymentTestSet")
    public void checkPayment( String inputPaymentStatus, Class expectedResponseClass, FailureCause expectedFailureCause ) throws PluginTechnicalException {
        // Mock payment state
        Mockito.doReturn(createMockedSlimpayPaymentIn( inputPaymentStatus ))
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );
        // Mock payment-issues
        Mockito.doReturn( "AC04" )
                .when( httpClient )
                .getPaymentRejectReason( any(PartnerConfiguration.class), anyString() );

        // when: calling method checkPayment()
        PaymentResponse response = service.checkPayment(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED ) );

        // then: response is as expected
        assertTrue( expectedResponseClass.isInstance( response ) );
        if( expectedResponseClass == PaymentResponseFailure.class ){
            assertEquals( expectedFailureCause, ((PaymentResponseFailure)response).getFailureCause() );
            assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        }
    }

    @Test
    public void checkPayment_exception() throws PluginTechnicalException {
        // Mock exception during searchPayment()
        Mockito.doThrow( new InvalidDataException("Exception message longer than 50 characters (which is the maximum allowed", "request.partnerConfiguration") )
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );

        // when: calling method checkPayment()
        PaymentResponse response = service.checkPayment(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED ) );

        // then: response is a failure, with valid failure cause and error code
        assertTrue( response instanceof PaymentResponseFailure );
        assertNotNull( ((PaymentResponseFailure)response).getFailureCause() );
        assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        assertTrue( ((PaymentResponseFailure)response).getErrorCode().length() <= 50 );
    }

    @Test
    public void checkPayment_partnerError() throws PluginTechnicalException {
        // Mock partner error returned by searchPayment()
        Mockito.doReturn( BeansUtils.createMockedSlimpayFailureResponse() )
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );

        // when: calling method checkPayment()
        PaymentResponse response = service.checkPayment(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED ) );

        // then: response is a failure, with valid failure cause and error code
        assertTrue( response instanceof PaymentResponseFailure );
        assertNotNull( ((PaymentResponseFailure)response).getFailureCause() );
        assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        assertTrue( ((PaymentResponseFailure)response).getErrorCode().length() <= 50 );
    }


    /**
     * Test set build from the documentation :
     * https://payline.atlassian.net/wiki/spaces/APMSDK/pages/1192919570/Slimpay+-+Analyse#Slimpay-Analyse-MappingdeserreursSlimpay
     *
     * Test set is only partial (randomly chosen).
     */
    private static Stream<Arguments> checkPaymentIssueTestSet() {
        return Stream.of(
                Arguments.of("AC01", FailureCause.INVALID_DATA),
                Arguments.of("AC06", FailureCause.REFUSED),
                // Arguments.of("AG02", FailureCause.INVALID_FIELD_FORMAT), // TODO: change the mapping
                Arguments.of("CNOR", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("FOCR", FailureCause.CANCEL)
        );
    }

    @ParameterizedTest
    @MethodSource("checkPaymentIssueTestSet")
    public void checkPaymentIssue( String returnReasonCode, FailureCause expectedFailureCause ) throws PluginTechnicalException {
        // Mock payment state : 'rejected', the only case in which we get the payment-issues
        Mockito.doReturn(createMockedSlimpayPaymentIn( PaymentExecutionStatus.REJECTED ))
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );
        // Mock payment-issue
        Mockito.doReturn( returnReasonCode )
                .when( httpClient )
                .getPaymentRejectReason( any(PartnerConfiguration.class), anyString() );

        // when: calling method checkPayment()
        PaymentResponse response = service.checkPayment(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED ) );

        // then: response is a failure and the failure cause is as expected
        assertTrue( response instanceof PaymentResponseFailure );
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        assertEquals( expectedFailureCause, failureResponse.getFailureCause() );
        assertNotNull( failureResponse.getErrorCode() );
    }

    @Test
    public void checkPaymentIssue_exception() throws PluginTechnicalException {
        // Mock payment state : 'rejected', the only case in which we get the payment-issues
        Mockito.doReturn(createMockedSlimpayPaymentIn( PaymentExecutionStatus.REJECTED ))
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );
        // Mock exception during getPaymentRejectReason()
        Mockito.doThrow( new HttpCallException("Exception message longer than 50 characters (which is the maximum allowed", "SlimpayHttpClient.getPaymentRejectReason") )
                .when( httpClient )
                .getPaymentRejectReason( any(PartnerConfiguration.class), anyString() );

        // when: calling method checkPayment()
        PaymentResponse response = service.checkPayment(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED ) );

        // then: response is a failure, with valid failure cause and error code
        assertTrue( response instanceof PaymentResponseFailure );
        assertNotNull( ((PaymentResponseFailure)response).getFailureCause() );
        assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        assertTrue( ((PaymentResponseFailure)response).getErrorCode().length() <= 50 );
    }

    @Test
    public void checkPaymentIssue_unknownReasonCode() throws PluginTechnicalException {
        // Mock payment state : 'rejected', the only case in which we get the payment-issues
        Mockito.doReturn(createMockedSlimpayPaymentIn( PaymentExecutionStatus.REJECTED ))
                .when( httpClient )
                .searchPayment( any(PartnerConfiguration.class),any(ContractConfiguration.class), anyString(), anyString(), anyString() );
        // Mock getPaymentRejectReason() returns null
        Mockito.doReturn( "UNKNOWN" )
                .when( httpClient )
                .getPaymentRejectReason( any(PartnerConfiguration.class), anyString() );

        // when: calling method checkPayment()
        PaymentResponse response = service.checkPayment(TestUtils.PARTNER_CONFIGURATION, TestUtils.CONTRACT_CONFIGURATION, TRANSACTION_ID, TestUtils.AMOUNT, TestUtils.createDefaultBuyer(), createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED ) );

        // then: response is a failure, with valid failure cause and error code
        assertTrue( response instanceof PaymentResponseFailure );
        assertNotNull( ((PaymentResponseFailure)response).getFailureCause() );
        assertNotNull( ((PaymentResponseFailure)response).getErrorCode() );
        assertTrue( ((PaymentResponseFailure)response).getErrorCode().length() <= 50 );
    }

}
