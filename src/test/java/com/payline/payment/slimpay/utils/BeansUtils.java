package com.payline.payment.slimpay.utils;


import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.common.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.bean.common.request.SlimpayPaymentRequest;

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
                .withCreditor(new SlimpayOrderRequest.Creditor("creditor1"))
                .withSubscriber(new SlimpayOrderRequest.Subscriber("Client2"))
                .withItems(items)
                .withLocale("FR")
                .withPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .withFailureUrl("failure.url.com")
                .withSuccessUrl("success.url.com")
                .build();
    }


}
