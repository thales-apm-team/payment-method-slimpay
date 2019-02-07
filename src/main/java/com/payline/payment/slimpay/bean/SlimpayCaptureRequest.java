package com.payline.payment.slimpay.bean;

import com.payline.payment.slimpay.utils.InvalidRequestException;
import com.payline.payment.slimpay.utils.SlimpayCardConstants;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;

public class SlimpayCaptureRequest extends SlimpayRequest {
    private String paymentId;

    public SlimpayCaptureRequest(RedirectionPaymentRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.paymentId= request.getRequestContext().getRequestData().get(SlimpayCardConstants.PSC_ID);
    }

    public SlimpayCaptureRequest(String paymentId, ContractConfiguration configuration) throws InvalidRequestException {
        super(configuration);
        this.paymentId = paymentId;
    }

    public SlimpayCaptureRequest(TransactionStatusRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.paymentId = request.getTransactionId();
    }

    public String getPaymentId() {
        return paymentId;
    }
}
