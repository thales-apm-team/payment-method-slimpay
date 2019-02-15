package com.payline.payment.slimpay.utils;


import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.utils.properties.constants.OrderStatus;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Rel;
import com.slimpay.hapiclient.hal.Resource;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

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
    public static SlimpayOrderResponse createMockedSlimpayOrderResponse(String state) {
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

    public static SlimpayOrderResponse createMockedSlimpayOrderResponseOpen() {
        return createMockedSlimpayOrderResponse(OrderStatus.OPEN_RUNNING);
    }

    public static SlimpayOrderResponse createMockedSlimpayOrderResponseClosed() {
        return createMockedSlimpayOrderResponse(OrderStatus.CLOSED_COMPLETED);
    }

    public static SlimpayOrderResponse createMockedSlimpayOrderResponseClosedAborted() {
        return createMockedSlimpayOrderResponse(OrderStatus.CLOSED_ABORTED);
    }

    public static SlimpayOrderResponse createMockedSlimpayOrderResponseClosedAbortedByClient() {
        return createMockedSlimpayOrderResponse(OrderStatus.CLOSED_ABORTED_BY_CLIENT);
    }

    public static SlimpayFailureResponse createMockedSlimpayFailureResponse() {
        String jsonError = "{\n" +
                "   \"code\": 901,\n" +
                "   \"message\": \"Duplicate order : order Y-ORDER-REF- for creditor paylinemerchanttest1 already exists\"\n" +
                "}";
        return SlimpayFailureResponse.fromJson(jsonError);


    }

    public static SlimpayPaymentResponse createMockedSlimpayPaymentOut(String executionStatus) {
        return SlimpayPaymentResponse.Builder.fromJson("{\n" +
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

    public static SlimpayPaymentResponse createMockedSlimpayPaymentOutTopProcess() {
        return createMockedSlimpayPaymentOut(PaymentExecutionStatus.TOP_PROCESS);
    }

    public static SlimpayPaymentResponse createMockedSlimpayPaymentOutProcessed() {
        return createMockedSlimpayPaymentOut(PaymentExecutionStatus.PROCESSED);
    }

    public static SlimpayPaymentResponse createMockedSlimpayPaymentOutRejected() {
        return createMockedSlimpayPaymentOut(PaymentExecutionStatus.REJECTED);
    }

    public static SlimpayFailureResponse createMockedSlimpayPaymentOutError() {
        String jsonError = "{\n" +
                "   \"code\": 901,\n" +
                "   \"message\": \"Duplicate order : order Y-ORDER-REF- for creditor paylinemerchanttest1 already exists\"\n" +
                "}";
        return SlimpayFailureResponse.fromJson(jsonError);
    }

    public static Resource getMockedResource() {
        JsonObject state = Json.createObjectBuilder()
                .add("id", 1)
                .add("reference", 1)
                .add("state", 1)
                .add("locale", 1)
                .build();

        Map<Rel, Object> links = new HashMap<>();
        Map<Rel, Object> embbeded = new HashMap<>();
        links.put(new CustomRel("https://api.slimpay.net/alps#extended-user-approval"), "https://api.preprod.slimpay.com/creditors/paylinemerchanttest1/orders/HDEV-1550076312981/extended-user-approval{?mode}");
        links.put(new CustomRel("https://api.slimpay.net/alps#cancel-order"), "https://api.preprod.slimpay.com/orders/c6542999-2fae-11e9-9d34-000000000000/cancellation");
        links.put(new CustomRel("https://api.slimpay.net/alps#get-order-items"), "https://api.preprod.slimpay.com/orders/c6542999-2fae-11e9-9d34-000000000000/order-items");

        String resourceJson = "";

        return Resource.fromJson(resourceJson);


    }

}
