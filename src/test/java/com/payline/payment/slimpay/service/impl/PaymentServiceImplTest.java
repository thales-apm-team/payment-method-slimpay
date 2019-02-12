package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.payline.payment.slimpay.utils.TestUtils.createBadPaymentRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createDefaultPaymentRequest;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentServiceImplTest {


    //    @Spy
    SlimpayHttpClient httpClient;

    //    @InjectMocks
//    PaymentServiceImpl service = new PaymentServiceImpl();
    PaymentServiceImpl service = new PaymentServiceImpl();
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();


//        @BeforeAll
    public void setup() {
        service = new PaymentServiceImpl();
//        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void paymentRequestOK() throws Exception {

        PaymentRequest request = createDefaultPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);
        System.out.println(response);
        Assertions.assertTrue(response.getClass() == PaymentResponseRedirect.class);

        PaymentResponseRedirect responseRedirect = (PaymentResponseRedirect) response;
        //Assert we have confirmation Url
        Assertions.assertNotNull(responseRedirect.getRedirectionRequest().getUrl());
        System.out.println(responseRedirect.getRedirectionRequest().getUrl());



    }

    @Test
    public void paymentRequestReferenceDuplicated() throws Exception {
        PaymentRequest request = createBadPaymentRequest();
        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertTrue(response.getClass() == PaymentResponseFailure.class);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getFailureCause());
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
        Assertions.assertNotNull(responseFailure.getErrorCode());



    }

}
