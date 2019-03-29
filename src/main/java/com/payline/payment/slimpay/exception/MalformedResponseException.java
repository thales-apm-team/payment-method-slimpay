package com.payline.payment.slimpay.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class MalformedResponseException extends PluginTechnicalException {

    private static final String ERROR_CODE = "Unable to parse JSON content";

    /**
     * Exception constructor
     * @param exception Initial exception thrown
     */
    public MalformedResponseException(Exception exception) {
        super(exception, ERROR_CODE);
    }

    @Override
    public FailureCause getFailureCause() {
        return FailureCause.COMMUNICATION_ERROR;
    }
}