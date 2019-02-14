package com.payline.payment.slimpay.bean.common;

import com.google.gson.Gson;

public class CardAlias {

    private String id;
    private String reference;
    private String status;
    private String cardExpirationDate;

    public String getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getStatus() {
        return status;
    }

    public String getCardExpirationDate() {
        return cardExpirationDate;
    }


    public static CardAlias fromJson(String json){
        Gson parser = new Gson();
        return parser.fromJson(json, CardAlias.class);
    }
}
