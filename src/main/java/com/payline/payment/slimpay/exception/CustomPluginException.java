package com.payline.payment.slimpay.exception;

import com.payline.pmapi.bean.common.FailureCause;

/**
 * Additional implementation of <code><PluginTechniqueException/code> that gives simple and full control over the
 * <code>errorCode</code> and <code><failureCause/code> that will be passed on to a <code>ResponseFailure</code>.
 */
public class CustomPluginException extends PluginTechnicalException {

    private final FailureCause failureCause;

    public CustomPluginException(FailureCause failureCause, String message) {
        super(message, message);
        this.failureCause = failureCause;
    }

    @Override
    public FailureCause getFailureCause() {
        return this.failureCause;
    }
}
