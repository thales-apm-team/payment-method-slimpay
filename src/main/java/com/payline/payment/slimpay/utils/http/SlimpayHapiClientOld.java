package com.payline.payment.slimpay.utils.http;

import com.google.gson.Gson;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.slimpay.hapiclient.exception.HttpClientErrorException;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;

import java.util.Map;

//use  client monext
//use it or not for http call??
//todo find a way to instatiate singleton without param  ( do make instance  + method init (param... )  ?)
public class SlimpayHapiClientOld {

    //données pour tester  à  supprimer  ASAP
    private static final String API_URL = "https://api.preprod.slimpay.com";
    private static final String API_PROFILE = "https://api.slimpay.net/alps/v1";
    private static final String APP_ID = "monextreferral01";
    private static final String APP_SECRET = "n32cXdaS0ZOACV8688ltKovAO6lquL4wKjZHnvyO";
    private static final String CONTENT_TYPE = "application/json";
    private Gson parser;

    // The client used to send request
    private HapiClient client;

    public HapiClient getClient() {
        return client;
    }


        /**
         * Singleton Holder
         */
    private static class SingletonHolder {
        private static final SlimpayHapiClientOld INSTANCE = new SlimpayHapiClientOld(API_URL,API_PROFILE,APP_ID,APP_SECRET);
    }



    /**
     * @return the singleton instance
     */
    public static SlimpayHapiClientOld getInstance() {
        return SingletonHolder.INSTANCE;
    }
    /**
     * Instanciate a HTTP client developed by SlimPay
     * @param apiUrl the SlimPay API URL
     * @param apiProfile the SlimPay Profile URL
     * @param appId the creditor id
     * @param appSecret the creditor password
     */
    public SlimpayHapiClientOld(String apiUrl, String apiProfile, String appId, String appSecret){
        this.client = new HapiClient.Builder()
                .setApiUrl( apiUrl)
                .setProfile(apiProfile)
                .setAuthenticationMethod(
                        new Oauth2BasicAuthentication.Builder()
                                .setTokenEndPointUrl("/oauth/token")
                                .setUserid(appId)
                                .setPassword(appSecret)
                                .build()
                )
                .build();

    }



    /**
     *  Add simples parameters to a follow request from a <String, ContractProperty> map
     * @param followBuilder
     * @param urlVariable
     * @return
     */
    public Follow.Builder addParametersToFollow (Follow.Builder followBuilder, Map<String, ContractProperty> urlVariable){

        //Boucle pour ajouter toutes les contract properties a la requete
        for (Map.Entry<String,ContractProperty> param : urlVariable.entrySet()) {
            String key = param.getKey();
            String value = param.getValue().getValue();
            followBuilder =  followBuilder.setUrlVariable(key, value);

        }

        return followBuilder;

    }

    /**
     * Send a Post method
     *
     * @param follow the request body of the action
     * @return A Resource Object composed by a JSON Object and two maps
     * @throws HttpException
     */
    public Resource doPost (Follow follow) throws HttpException {
        try {
            System.err.println(follow.getUrlVariables());

            return  this.getClient().send(follow);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.err.println(e.getResponseBody());
        }
        return null;
    }

    /**
     * Send a Post method
     *
     * @param follow the request body of the action
     * @return A Resource Object composed by a JSON Object and two maps
     * @throws HttpException
     */
    public Resource doGet (Follow follow) throws HttpException {
        try {
            System.err.println(follow.getUrlVariables());

            return  this.getClient().send(follow);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.err.println(e.getResponseBody());
        }
        return null;
    }
}
