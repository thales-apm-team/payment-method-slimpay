package com.payline.payment.slimpay.bean.common;

import com.google.gson.Gson;

public abstract class SlimpayBean {

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
