package com.payline.payment.slimpay.bean.common.request;

import com.payline.payment.slimpay.bean.common.Creditor;
import com.payline.payment.slimpay.bean.common.SlimPayOrderItem;
import com.payline.payment.slimpay.bean.common.Subscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultOrderItemMandate;
import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultOrderItemPayment;

public class SlimpayOrderRequestTest {

    private SlimpayOrderRequest orderRequest;

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
    public void slimpayOrderRequestKO() {


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
    }

}
