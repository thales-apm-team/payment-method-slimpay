package com.payline.payment.slimpay.bean.common.request;

import com.payline.payment.slimpay.exception.InvalidRequestException;
import com.payline.pmapi.bean.payment.ContractConfiguration;

public class SlimpayPaymentRequest extends SlimpayRequest {


    SlimpayPaymentRequest(ContractConfiguration configuration) throws InvalidRequestException {
        super(configuration);
    }
}
