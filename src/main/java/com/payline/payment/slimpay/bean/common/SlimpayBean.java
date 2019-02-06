package com.payline.payment.slimpay.bean.common;

import com.google.gson.Gson;
import com.slimpay.hapiclient.http.JsonBody;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

public abstract class SlimpayBean {

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * create a JsonBody object of this Bean
     * @return
     */
    public JsonBody toJsonBody(){
        JsonReader reader = Json.createReader(new StringReader( this.toString() ));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        return new JsonBody(jsonObject);
    }
}
