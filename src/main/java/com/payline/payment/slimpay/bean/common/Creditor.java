package com.payline.payment.slimpay.bean.common;

public class Creditor {
    private String reference;

    public Creditor(String ref) {
        this.reference = ref;
    }

    public String getReference() {
        return reference;
    }
}
