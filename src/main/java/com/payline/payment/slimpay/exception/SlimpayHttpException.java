package com.payline.payment.slimpay.exception;

import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.logger.LogManager;
import com.slimpay.hapiclient.exception.HttpException;
import org.apache.logging.log4j.Logger;

public class SlimpayHttpException extends PluginTechnicalException {

    private static final Logger LOGGER = LogManager.getLogger(SlimpayHttpException.class);


    /**
     * @param e the original catched Exception
     */
    public SlimpayHttpException(HttpException e) {
        super(e);
        LOGGER.error(this.toString());

    }

    @Override
    public FailureCause getFailureCause() {
        return SlimpayErrorMapper.handleSlimpayError(slimpayError);
    }

}
