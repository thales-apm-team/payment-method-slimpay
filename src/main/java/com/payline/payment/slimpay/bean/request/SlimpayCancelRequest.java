package com.payline.payment.slimpay.bean.request;

import com.payline.payment.slimpay.bean.common.SlimpayBean;

public class SlimpayCancelRequest extends SlimpayBean {

    public  enum reasonCode {
        CUST;
    }
    private String returnReasonCode;

    public String getReturnReasonCode() {
        return returnReasonCode;
    }

    public SlimpayCancelRequest(SlimpayCancelRequest.reasonCode reasonCode) {
        this.returnReasonCode = reasonCode.name();
    }
}
