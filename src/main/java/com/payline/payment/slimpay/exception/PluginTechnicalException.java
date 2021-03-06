package com.payline.payment.slimpay.exception;

import com.google.gson.Gson;
import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.slimpay.hapiclient.exception.HttpException;
import org.apache.logging.log4j.Logger;

public class PluginTechnicalException extends Exception {

    private static final Logger LOGGER = LogManager.getLogger(PluginTechnicalException.class);

    private static final int MAX_LENGTH = 50;
    private static final String NO_TRANSACTION = "NO_TRANSACTION_YET";

    private final String message;

    protected final SlimpayError slimpayError;


    protected final String errorCodeOrLabel;

    public PluginTechnicalException(String message, String errorCodeOrLabel) {
        super();
        this.message = message;
        this.errorCodeOrLabel = errorCodeOrLabel;
        slimpayError = null;
        LOGGER.error(message);
    }


    /**
     * @param e                the original catched Exception
     * @param errorCodeOrLabel informations about the exception : 'Class.Method.Exception'
     */
    public PluginTechnicalException(Exception e, String errorCodeOrLabel) {
        super(e);
        this.message = e == null ? "" : e.getMessage();
        this.errorCodeOrLabel = errorCodeOrLabel;
        slimpayError = null;
        LOGGER.error(errorCodeOrLabel, e);
    }

    /**
     * @param e the original catched Exception
     */
    public PluginTechnicalException(HttpException e) {
        super(e);
        this.message = e.getMessage();
        String errorString = e.getResponseBody();
        slimpayError = SlimpayError.fromJson(errorString);
        this.errorCodeOrLabel = slimpayError == null ? null : slimpayError.toPaylineError();
        LOGGER.error(errorCodeOrLabel, e);
    }


    public PaymentResponseFailure toPaymentResponseFailure() {
        return toPaymentResponseFailure(NO_TRANSACTION);
    }


    public PaymentResponseFailure toPaymentResponseFailure(String partnerTransactionId) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(getFailureCause())
                .withErrorCode(getTruncatedErrorCodeOrLabel())
                .withPartnerTransactionId(partnerTransactionId)
                .build();
    }

    public RefundResponseFailure toRefundResponseFailure(String partnerTransactionId) {
        return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                .withFailureCause(getFailureCause())
                .withErrorCode(getTruncatedErrorCodeOrLabel())
                .withPartnerTransactionId(partnerTransactionId)
                .build();
    }

    public ResetResponseFailure toResetResponseFailure(String partnerTransactionId){
        return ResetResponseFailure.ResetResponseFailureBuilder.aResetResponseFailure()
                .withFailureCause(getFailureCause())
                .withErrorCode(getTruncatedErrorCodeOrLabel())
                .withPartnerTransactionId(partnerTransactionId)
                .build();
    }

    public String getErrorCodeOrLabel() {
        return errorCodeOrLabel;
    }


    public String getTruncatedErrorCodeOrLabel() {
        return PluginUtils.truncate(this.getErrorCodeOrLabel(), MAX_LENGTH);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public FailureCause getFailureCause() {
        return FailureCause.PARTNER_UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this.toPaymentResponseFailure());
    }
}
