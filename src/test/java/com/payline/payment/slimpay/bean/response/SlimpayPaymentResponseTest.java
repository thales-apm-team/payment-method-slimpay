package com.payline.payment.slimpay.bean.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SlimpayPaymentResponseTest {

    private SlimpayPaymentResponse paymentResponse;

    @Test
    public void paymentResponseFromJsontest() {

        String json = "{\n" +
                "   \"_links\":    {\n" +
                "      \"self\": {\"href\": \"https://api.preprod.slimpay.com/payments/604c28a4-3368-11e9-bf72-000000000000\"},\n" +
                "      \"profile\": {\"href\": \"https://api.preprod.slimpay.com/alps/v1/payments\"},\n" +
                "      \"https://api.slimpay.net/alps#get-creditor\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1\"},\n" +
                "      \"https://api.slimpay.net/alps#get-subscriber\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1/subscribers/Client2\"},\n" +
                "      \"https://api.slimpay.net/alps#patch-payment\": {\"href\": \"https://api.preprod.slimpay.com/payments/604c28a4-3368-11e9-bf72-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-debtor-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/payments/604c28a4-3368-11e9-bf72-000000000000/debtor-bank-account\"},\n" +
                "      \"https://api.slimpay.net/alps#get-mandate\": {\"href\": \"https://api.preprod.slimpay.com/mandates/5da37704-3368-11e9-980d-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-destination-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/bank-accounts/323d8f33-02b2-11e9-8506-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-origin-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/bank-accounts/5d4418f3-3368-11e9-980d-000000000000\"}\n" +
                "   },\n" +
                "   \"id\": \"604c28a4-3368-11e9-bf72-000000000000\",\n" +
                "   \"scheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"reference\": \"Y-PAYMENT-REF-1550485880689\",\n" +
                "   \"direction\": \"IN\",\n" +
                "   \"amount\": \"100.00\",\n" +
                "   \"currency\": \"EUR\",\n" +
                "   \"label\": \"The label\",\n" +
                "   \"sequenceType\": \"RCUR\",\n" +
                "   \"state\": \"accepted\",\n" +
                "   \"executionStatus\": \"toprocess\",\n" +
                "   \"replayCount\": 0,\n" +
                "   \"executionDate\": \"2019-02-19T23:00:00.000+0000\",\n" +
                "   \"dateCreated\": \"2019-02-18T10:31:40.000+0000\",\n" +
                "   \"confirmed\": false\n" +
                "}";

        paymentResponse = SlimpayPaymentResponse.fromJson(json);
        Assertions.assertNotNull(paymentResponse.getId());
        Assertions.assertNotNull(paymentResponse.getReference());
        Assertions.assertNotNull(paymentResponse.getDirection());
        Assertions.assertNotNull(paymentResponse.getAmount());
        Assertions.assertNotNull(paymentResponse.getCurrency());
        Assertions.assertNotNull(paymentResponse.getLabel());
        Assertions.assertNotNull(paymentResponse.getSequenceType());
        Assertions.assertNotNull(paymentResponse.getState());
        Assertions.assertNotNull(paymentResponse.getExecutionStatus());
        Assertions.assertNotNull(paymentResponse.getReplayCount());
        Assertions.assertNotNull(paymentResponse.getExecutionDate());
        Assertions.assertNotNull(paymentResponse.getDateCreated());
        Assertions.assertFalse(paymentResponse.isConfirmed());


    }
}
