package com.payline.payment.slimpay.exception;

public class DecryptException extends PluginTechnicalException {

    /**
     * @param e      the original catched Exception
     * @param origin method and type of exception : 'Class.Method.Exception'
     */
    public DecryptException(Exception e, String origin) {
        super(e, origin);
    }

}
