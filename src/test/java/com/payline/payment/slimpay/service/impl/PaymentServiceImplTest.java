package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.MalformedResponseException;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.OrderStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.slimpay.hapiclient.http.JsonBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.*;
import static com.payline.payment.slimpay.utils.TestUtils.createBadPaymentRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createDefaultPaymentRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl service;

    @Mock
    SlimpayHttpClient httpClient;

    @BeforeAll
    public void setup() {
        service = new PaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void paymentRequestOK() throws Exception {
        SlimpayOrderResponse responseMocked = createMockedSlimpayOrderResponse( OrderStatus.OPEN_RUNNING );
        when(httpClient.createOrder(any(PartnerConfiguration.class), any(JsonBody.class))).thenReturn(responseMocked);

        PaymentRequest request = createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseRedirect.class, response.getClass());
        PaymentResponseRedirect responseRedirect = (PaymentResponseRedirect) response;
        //Assert we have confirmation Url
        Assertions.assertNotNull(responseRedirect.getRedirectionRequest().getUrl());
        Assertions.assertNotNull(responseRedirect.getPartnerTransactionId());
        Assertions.assertNotNull(responseRedirect.getRequestContext());

    }

    @Test
    public void paymentRequestKO() throws Exception {
        SlimpayFailureResponse responseMocked = createMockedSlimpayFailureResponse();
        when(httpClient.createOrder(any(PartnerConfiguration.class), any(JsonBody.class))).thenReturn(responseMocked);

        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());

    }

    @Test
    public void paymentRequestKOExceptionMalFormedUrl() throws Exception {
        SlimpayOrderResponse responseMocked = createMockedSlimpayOrderResponse( OrderStatus.CLOSED_COMPLETED );
        responseMocked.setUrlApproval("foo");

        when(httpClient.createOrder(any(PartnerConfiguration.class), any(JsonBody.class))).thenReturn(responseMocked);
        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());

    }





    @Test
    public void paymentRequestKOException() throws Exception {
        when(httpClient.createOrder(any(PartnerConfiguration.class), any(JsonBody.class))).thenThrow(new HttpCallException("this is an error", "foo"));

        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());

    }


    @Test
    public void paymentRequestResponseNull() throws Exception {
        Mockito.doReturn(null).when(httpClient).createOrder(any(PartnerConfiguration.class), any(JsonBody.class));

        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());

    }


    @Test
    public void paymentRequestNullRequest() throws Exception {
        PaymentResponse response = service.paymentRequest(null);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());

    }

    @Test
    public void paymentRequestKOExceptionMalformedUrl() throws Exception {
        when(httpClient.createOrder(any(PartnerConfiguration.class), any(JsonBody.class))).thenThrow(new MalformedResponseException(new HttpCallException("this is an error", "foo")));

        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getPartnerTransactionId());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());

    }

}
