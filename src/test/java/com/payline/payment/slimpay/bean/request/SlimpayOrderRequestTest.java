package com.payline.payment.slimpay.bean.request;

import com.payline.payment.slimpay.bean.common.Creditor;
import com.payline.payment.slimpay.bean.common.SlimPayOrderItem;
import com.payline.payment.slimpay.bean.common.Subscriber;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultOrderItemMandate;
import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultOrderItemPayment;
@PrepareForTest({SlimpayOrderRequest.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlimpayOrderRequestTest {

    private SlimpayOrderRequest orderRequest;
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {

        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(SlimpayOrderRequest.class, "LOGGER", mockLogger);
    }

    @Test
    public void slimpayOrderRequestOK() {

        orderRequest = SlimpayOrderRequest.Builder.aSlimPayOrderRequestBuilder()
                .withReference("ORDER-123")
                .withCreditor(new Creditor("creditor1"))
                .withSubscriber(new Subscriber("Client2"))
                .withFailureUrl("failure.url.com")
                .withSuccessUrl("success.url.com")
                .withItems(new SlimPayOrderItem[]{
                        createDefaultOrderItemMandate(),
                        createDefaultOrderItemPayment()
                })
                .withLocale("FR")
                .withPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .build();
        String requestJson = orderRequest.toString();
        //Assert Json is well formed
        Assertions.assertTrue(requestJson.contains("reference"));
        Assertions.assertTrue(requestJson.contains("creditor"));
        Assertions.assertTrue(requestJson.contains("subscriber"));
        Assertions.assertTrue(requestJson.contains("items"));
        Assertions.assertTrue(requestJson.contains("paymentScheme"));
    }


    @Test
    public void slimpayOrderRequestEmpty() {


        orderRequest = SlimpayOrderRequest.Builder.aSlimPayOrderRequestBuilder()
                .build();
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimpayOrderRequest.CREDITOR_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimpayOrderRequest.SUBSCRIBER_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimpayOrderRequest.ITEMS_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimpayOrderRequest.FAIL_URL_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimpayOrderRequest.SUCCESS_URL_WARN));

    }

}
