package com.payline.payment.slimpay.bean.response;

import com.payline.payment.slimpay.exception.MalformedResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SlimpayOrderResponseTest {

    private SlimpayOrderResponse orderResponse;


    @Test
    public void  OrderResponseFromJsonTest() throws MalformedResponseException {

        String json = ("{\n" +
                "   \"_links\":    {\n" +
                "      \"self\": {\"href\": \"https://api.preprod.slimpay.com/orders/ff4ea3a6-303e-11e9-9d34-000000000000\"},\n" +
                "      \"profile\": {\"href\": \"https://api.preprod.slimpay.com/alps/v1/orders\"},\n" +
                "      \"https://api.slimpay.net/alps#get-creditor\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1\"},\n" +
                "      \"https://api.slimpay.net/alps#get-subscriber\": {\"href\": \"https://api.preprod.slimpay.com/orders/ff4ea3a6-303e-11e9-9d34-000000000000/subscriber\"},\n" +
                "      \"https://api.slimpay.net/alps#get-order-items\": {\"href\": \"https://api.preprod.slimpay.com/orders/ff4ea3a6-303e-11e9-9d34-000000000000/order-items\"},\n" +
                "      \"https://api.slimpay.net/alps#user-approval\": {\"href\": \"https://checkout.preprod.slimpay.com/userApproval?accessCode=spi5LZZtKKSEvMk3ogLghiOzAFUsKb1cTznTeh88yEHM5ES31r8Dm3kx21tAJF\"},\n" +
                "      \"https://api.slimpay.net/alps#extended-user-approval\":       {\n" +
                "         \"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1/orders/Y-ORDER-REF-1550138270755/extended-user-approval{?mode}\",\n" +
                "         \"templated\": true\n" +
                "      },\n" +
                "      \"https://api.slimpay.net/alps#cancel-order\": {\"href\": \"https://api.preprod.slimpay.com/orders/ff4ea3a6-303e-11e9-9d34-000000000000/cancellation\"}\n" +
                "   },\n" +
                "   \"id\": \"ff4ea3a6-303e-11e9-9d34-000000000000\",\n" +
                "   \"reference\": \"Y-ORDER-REF-1550138270755\",\n" +
                "   \"state\": \"open.running\",\n" +
                "   \"locale\": \"fr\",\n" +
                "   \"started\": true,\n" +
                "   \"dateCreated\": \"2019-02-14T09:57:54.083+0000\",\n" +
                "   \"dateStarted\": \"2019-02-14T09:57:54.083+0000\",\n" +
                "   \"paymentScheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"sendUserApproval\": true,\n" +
                "   \"checkoutActor\": \"end_user\"\n" +
                "}");

        orderResponse = SlimpayOrderResponse.fromJson(json);
        Assertions.assertEquals("ff4ea3a6-303e-11e9-9d34-000000000000",orderResponse.getId());
        Assertions.assertEquals("Y-ORDER-REF-1550138270755",orderResponse.getReference());
        Assertions.assertEquals("open.running",orderResponse.getState());
        Assertions.assertEquals("SEPA.DIRECT_DEBIT.CORE",orderResponse.getPaymentScheme());
        Assertions.assertEquals("fr",orderResponse.getLocale());
        Assertions.assertEquals("2019-02-14T09:57:54.083+0000",orderResponse.getDateCreated());
        Assertions.assertEquals("2019-02-14T09:57:54.083+0000",orderResponse.getDateStarted());
        Assertions.assertEquals("fr",orderResponse.getLocale());
        Assertions.assertEquals("end_user",orderResponse.getCheckoutActor());
        Assertions.assertTrue(orderResponse.isStarted());


    }

    @Test
    public void slimpayOrderResponseMalformedJson(){
            Assertions.assertThrows(MalformedResponseException.class,()->{
                String json = ("{malformed !!}");
                    orderResponse = SlimpayOrderResponse.fromJson(json);
                });
    }
}
