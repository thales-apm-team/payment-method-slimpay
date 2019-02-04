package com.payline.payment.slimpay.utils.http;


import com.slimpay.hapiclient.http.HapiClient;

/**
 * Created by Thales on  27/11/2018
 */
public class SlimpayHttpClient extends AbstractHttpClient {

    // The client provided by Slimpay HAPI
    private HapiClient hapiClient;
    /**
     * Instantiate a HTTP client with default values.
     */
    private SlimpayHttpClient() {
        super();
    }

    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final SlimpayHttpClient INSTANCE = new SlimpayHttpClient();
    }

    /**
     * @return the singleton instance
     */
    public static SlimpayHttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }


}
