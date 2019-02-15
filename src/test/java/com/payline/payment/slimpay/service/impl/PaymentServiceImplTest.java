package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayFailureResponse;
import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayOrderResponseOpen;
import static com.payline.payment.slimpay.utils.TestUtils.createBadPaymentRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createDefaultPaymentRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentServiceImplTest {


    @Spy
    SlimpayHttpClient httpClient;
//    @Spy
//    HapiClient hapiClient;

    @InjectMocks
    PaymentServiceImpl service;

    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();


    @BeforeAll
    public void setup() {
        service = new PaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void paymentRequestOK() throws Exception {
        SlimpayOrderResponse responseMocked = createMockedSlimpayOrderResponseOpen();
        Mockito.doReturn(responseMocked).when(httpClient).createOrder(Mockito.any(PaymentRequest.class),Mockito.any(JsonBody.class));

        PaymentRequest request = createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);
        System.out.println(response);
        Assertions.assertTrue(response.getClass() == PaymentResponseRedirect.class);

        PaymentResponseRedirect responseRedirect = (PaymentResponseRedirect) response;
        //Assert we have confirmation Url
        Assertions.assertNotNull(responseRedirect.getRedirectionRequest().getUrl());
        Assertions.assertNotNull(responseRedirect.getRedirectionRequest().getUrl());


    }

    @Test
    public void paymentRequestReferenceDuplicated() throws Exception {
        SlimpayFailureResponse responseMocked = createMockedSlimpayFailureResponse();
        Mockito.doReturn(responseMocked).when(httpClient).createOrder(Mockito.any(PaymentRequest.class),Mockito.any(JsonBody.class));

        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertTrue(response.getClass() == PaymentResponseFailure.class);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());


    }

}
