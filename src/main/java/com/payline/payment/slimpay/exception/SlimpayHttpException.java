package com.payline.payment.slimpay.exception;

import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.pmapi.bean.common.FailureCause;
import com.slimpay.hapiclient.exception.HttpException;

public class SlimpayHttpException extends PluginTechnicalException {

    /**
     * @param e the original catched Exception
     */
    public SlimpayHttpException(HttpException e) {
        super(e);
    }

    @Override
    public FailureCause getFailureCause() {
        return SlimpayErrorMapper.handleSlimpayError(slimpayError);
    }

}
