package com.payline.payment.slimpay.utils;


import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.exception.MalformedResponseException;
import com.payline.payment.slimpay.utils.properties.constants.OrderStatus;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Rel;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.JsonBody;
import com.slimpay.hapiclient.http.Method;

import javax.json.Json;
import java.util.Date;

public class BeansUtils {

    public static Mandate createDefaultMandate(String reference) {
        return Mandate.Builder.aMandateBuilder()
                .withSignatory(createDefaultSignatory())
                .withAction("create")
                .withReference(reference)
                .withPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .withStandard("SEPA")
                .withAction("sign")
                .build();
    }

    public static Payment createDefaultPayin(String reference) {
        return Payment.Builder.aPaymentBuilder()
                .withAction("create")
                .withReference(reference)
                .withScheme("SEPA.DIRECT_DEBIT.CORE")
                .withAmount("10")
                .withCurrency("EUR")
                .withLabel("default Paiement")
                .withDirection("IN")
                .build();
    }

    public static Signatory createDefaultSignatory() {
        return Signatory.Builder.aSignatoryBuilder()
                .withfamilyName("Doe")
                .withGivenName("John")
                .withHonorificPrefix("Mr")
                .withBilingAddress(createDefaultBillingAddress())
                .withEmail("toto@emailcom")
                .withTelephone("+33725262729")
                .build();
    }

    public static BillingAddress createDefaultBillingAddress() {
        return BillingAddress.Builder.aBillingAddressBuilder()
                .withStreet1("10 rue de la paix")
                .withStreet2("residence peace")
                .withCity("Versailles")
                .withCountry("FR")
                .withPostalCode("78000")
                .build();
    }


    public static SlimPayOrderItem createDefaultOrderItemMandate() {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withMandate(createDefaultMandate("XOXOXO"))
                .withType("signMandate")
                .withAction("sign")
                .build();
    }

    public static SlimPayOrderItem createDefaultOrderItemPayment() {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withPayin(createDefaultPayin("XOXOXO"))
                .withType("payment")
                .build();
    }

    public static SlimpayOrderRequest createDefaultSlimpayOrderRequest() {
        SlimPayOrderItem[] items = new SlimPayOrderItem[]{
                createDefaultOrderItemMandate(),
                createDefaultOrderItemPayment()
        };

        return SlimpayOrderRequest.Builder.aSlimPayOrderRequestBuilder()
                .withReference("ORDER-123")
                .withCreditor(new Creditor("creditor1"))
                .withSubscriber(new Subscriber("Client2"))
                .withItems(items)
                .withLocale("FR")
                .withPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .withFailureUrl("failure.url.com")
                .withSuccessUrl("success.url.com")
                .build();
    }

    //Mocked response
    public static SlimpayOrderResponse createMockedSlimpayOrderResponse(String state) throws MalformedResponseException {
        SlimpayOrderResponse OrderResponse = SlimpayOrderResponse.fromJson("{\n" +
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
                "   \"state\": \"" + state + "\",\n" +
                "   \"locale\": \"fr\",\n" +
                "   \"started\": true,\n" +
                "   \"dateCreated\": \"2019-02-14T09:57:54.083+0000\",\n" +
                "   \"dateStarted\": \"2019-02-14T09:57:54.083+0000\",\n" +
                "   \"paymentScheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"sendUserApproval\": true,\n" +
                "   \"checkoutActor\": \"end_user\"\n" +
                "}");
        OrderResponse.setUrlApproval("https://checkout.preprod.slimpay.com/userApproval?accessCode=spi5LZZtKKSEvMk3ogLghiOzAFUsKb1cTznTeh88yEHM5ES31r8Dm3kx21tAJF");
        return OrderResponse;
    }

    public static SlimpayFailureResponse createMockedSlimpayFailureResponse() throws MalformedResponseException {
        String jsonError = "{\n" +
                "   \"code\": 901,\n" +
                "   \"message\": \"Slimpay order : This error is mocked for test\"\n" +
                "}";
        return SlimpayFailureResponse.fromJson(jsonError);
    }

    public static SlimpayPaymentResponse createMockedSlimpayPaymentIn(String executionStatus)
            throws MalformedResponseException{
        return createMockedSlimpayPaymentIn( executionStatus, new Date(), null );
    }

    public static SlimpayPaymentResponse createMockedSlimpayPaymentIn(String executionStatus, Date executionDate,
            Boolean cancellable) throws MalformedResponseException {
        return SlimpayPaymentResponse.fromJson(" {  " +
                " \"id\": \"8212f471-3432-11e9-ad8f-000000000000\",\n" +
                "   \"scheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"reference\": \"HDEV-1550572705098\",\n" +
                "   \"direction\": \"IN\",\n" +
                "   \"amount\": \"408.00\",\n" +
                "   \"currency\": \"EUR\",\n" +
                "   \"label\": \"softDescriptor\",\n" +
                "   \"sequenceType\": \"RCUR\",\n" +
                "   \"state\": \"accepted\",\n" +
                "   \"executionStatus\": \"" + executionStatus + "\",\n" +
                "   \"replayCount\": 0,\n" +
                "   \"executionDate\": \"" + DateUtils.format( executionDate ) +"\",\n" +
                "   \"dateCreated\": \"2019-02-19T10:38:35.000+0000\",\n" +
                "   \"confirmed\": false" +
                "}", cancellable );
    }

    public static SlimpayPaymentResponse createMockedSlimpayPaymentOut(String executionStatus)  throws MalformedResponseException{
        return SlimpayPaymentResponse.fromJson("{\n" +
                "    \"id\": \"edbd987c-23e1-11e9-ad0d-000000000000\",\n" +
                "    \"scheme\": \"SEPA.CREDIT_TRANSFER\",\n" +
                "    \"reference\": \"REB-EXE-20190129-10189\",\n" +
                "    \"direction\": \"OUT\",\n" +
                "    \"amount\": \"0.01\",\n" +
                "    \"currency\": \"EUR\",\n" +
                "    \"label\": \"Virement Slimpay\",\n" +
                "    \"state\": \"accepted\",\n" +
                "    \"executionStatus\": \"" + executionStatus + "\",\n" +
                "    \"executionDate\": \"2019-01-28T23:00:00.000+0000\",\n" +
                "    \"dateCreated\": \"2019-01-29T16:21:27.471+0000\",\n" +
                "    \"confirmed\": false,\n" +
                "    \"category\": \"refund\",\n" +
                "    \"processor\": \"slimpay\"\n" +
                "}");
    }

    public static SlimpayFailureResponse createMockedSlimpayPaymentOutError() throws MalformedResponseException{
        String jsonError = "{\n" +
                "   \"code\": 901,\n" +
                "   \"message\": \"Duplicate order : order Y-ORDER-REF- for creditor paylinemerchanttest1 already exists\"\n" +
                "}";
        return SlimpayFailureResponse.fromJson(jsonError);
    }

    public static SlimpayFailureResponse createMockedCancelPaymentError() throws MalformedResponseException {
        String jsonError = "{\n" +
                "   \"code\": 903,\n" +
                "   \"message\": \"Illegal state : Cannot find cancellable direct debit with id=7232101\"\n" +
                "}";
        return SlimpayFailureResponse.fromJson(jsonError);
    }

    public static Resource createMockedResourcePayment(String state, String executionStatus){
        String resourceJson = "{\n" +
                "   \"_links\":    {\n" +
                "      \"self\": {\"href\": \"https://api.preprod.slimpay.com/payments/aeb615bc-35d7-11e9-ad8f-000000000000\"},\n" +
                "      \"profile\": {\"href\": \"https://api.preprod.slimpay.com/alps/v1/payments\"},\n" +
                "      \"https://api.slimpay.net/alps#get-creditor\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1\"},\n" +
                "      \"https://api.slimpay.net/alps#get-subscriber\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1/subscribers/Client2\"},\n" +
                "      \"https://api.slimpay.net/alps#patch-payment\": {\"href\": \"https://api.preprod.slimpay.com/payments/aeb615bc-35d7-11e9-ad8f-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-debtor-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/payments/aeb615bc-35d7-11e9-ad8f-000000000000/debtor-bank-account\"},\n" +
                "      \"https://api.slimpay.net/alps#get-mandate\": {\"href\": \"https://api.preprod.slimpay.com/mandates/ad81b42b-35d7-11e9-8de4-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-destination-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/bank-accounts/323d8f33-02b2-11e9-8506-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-origin-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/bank-accounts/c8d45521-2ea9-11e9-a9a1-000000000000\"}\n" +
                "   },\n" +
                "   \"id\": \"aeb615bc-35d7-11e9-ad8f-000000000000\",\n" +
                "   \"scheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"reference\": \"HDEV-1550753594140\",\n" +
                "   \"direction\": \"IN\",\n" +
                "   \"amount\": \"408.00\",\n" +
                "   \"currency\": \"EUR\",\n" +
                "   \"label\": \"softDescriptor\",\n" +
                "   \"sequenceType\": \"RCUR\",\n" +
                "   \"state\": \""+state+"\",\n" +
                "   \"executionStatus\": \""+executionStatus+"\",\n" +
                "   \"replayCount\": 0,\n" +
                "   \"executionDate\": \"2019-02-24T23:00:00.000+0000\",\n" +
                "   \"dateCreated\": \"2019-02-21T12:53:28.000+0000\",\n" +
                "   \"confirmed\": false\n" +
                "}";

        return Resource.fromJson(resourceJson);
    }

    public static Resource createMockedResourceOrder(String state){
        String resourceJson = "{\n" +
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
                "   \"state\": \"" + state + "\",\n" +
                "   \"locale\": \"fr\",\n" +
                "   \"started\": true,\n" +
                "   \"dateCreated\": \"2019-02-14T09:57:54.083+0000\",\n" +
                "   \"dateStarted\": \"2019-02-14T09:57:54.083+0000\",\n" +
                "   \"paymentScheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"sendUserApproval\": true,\n" +
                "   \"checkoutActor\": \"end_user\"\n" +
                "}";

        return Resource.fromJson(resourceJson);
    }

    public static Resource createEmptyMockedRessource() {
        return Resource.fromJson("{}");
    }

    public static Resource createResourceWithEmbeded(String state, String executionStatus){
        String resourceJson = "{\n" +
                "   \"_embedded\":    {\n" +
                "      \"self\": {\"href\": \"https://api.preprod.slimpay.com/payments/aeb615bc-35d7-11e9-ad8f-000000000000\"},\n" +
                "      \"profile\": {\"href\": \"https://api.preprod.slimpay.com/alps/v1/payments\"},\n" +
                "      \"https://api.slimpay.net/alps#get-creditor\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1\"},\n" +
                "      \"https://api.slimpay.net/alps#get-subscriber\": {\"href\": \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1/subscribers/Client2\"},\n" +
                "      \"https://api.slimpay.net/alps#patch-payment\": {\"href\": \"https://api.preprod.slimpay.com/payments/aeb615bc-35d7-11e9-ad8f-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-debtor-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/payments/aeb615bc-35d7-11e9-ad8f-000000000000/debtor-bank-account\"},\n" +
                "      \"https://api.slimpay.net/alps#get-mandate\": {\"href\": \"https://api.preprod.slimpay.com/mandates/ad81b42b-35d7-11e9-8de4-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-destination-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/bank-accounts/323d8f33-02b2-11e9-8506-000000000000\"},\n" +
                "      \"https://api.slimpay.net/alps#get-origin-bank-account\": {\"href\": \"https://api.preprod.slimpay.com/bank-accounts/c8d45521-2ea9-11e9-a9a1-000000000000\"}\n" +
                "   },\n" +
                "   \"id\": \"aeb615bc-35d7-11e9-ad8f-000000000000\",\n" +
                "   \"scheme\": \"SEPA.DIRECT_DEBIT.CORE\",\n" +
                "   \"reference\": \"HDEV-1550753594140\",\n" +
                "   \"direction\": \"IN\",\n" +
                "   \"amount\": \"408.00\",\n" +
                "   \"currency\": \"EUR\",\n" +
                "   \"label\": \"softDescriptor\",\n" +
                "   \"sequenceType\": \"RCUR\",\n" +
                "   \"state\": \""+state+"\",\n" +
                "   \"executionStatus\": \""+executionStatus+"\",\n" +
                "   \"replayCount\": 0,\n" +
                "   \"executionDate\": \"2019-02-24T23:00:00.000+0000\",\n" +
                "   \"dateCreated\": \"2019-02-21T12:53:28.000+0000\",\n" +
                "   \"confirmed\": false\n" +
                "}";

        return Resource.fromJson(resourceJson);
    }

    public static Follow createDefaultFollow(){
        Rel rel = new CustomRel("testFollow");

        return  new Follow.Builder(rel)
                .setMethod(Method.POST)
                .setMessageBody(new JsonBody(
                        Json.createObjectBuilder()
                                .add("creditor", Json.createObjectBuilder()
                                        .add("reference", "democreditor")
                                )
                                .add("subscriber", Json.createObjectBuilder()
                                        .add("reference", "subscriber5c6c36dac9fd0")
                                )
                ))
                .build();
    }
}
