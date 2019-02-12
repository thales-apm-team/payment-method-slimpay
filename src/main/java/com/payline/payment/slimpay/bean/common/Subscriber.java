package com.payline.payment.slimpay.bean.common;

public class Subscriber {

    public String reference;

    public Subscriber(String ref) {
        this.reference = ref;
    }

    public String getReference() {
        return reference;
    }
}
