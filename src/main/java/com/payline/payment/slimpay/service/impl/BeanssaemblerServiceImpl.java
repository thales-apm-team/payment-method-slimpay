package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Mandate;
import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.common.Signatory;
import com.payline.payment.slimpay.bean.common.SlimPayOrderItem;
import com.payline.payment.slimpay.service.BeanAssemblerService;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public class BeanssaemblerServiceImpl  implements BeanAssemblerService {
    @Override
    public Payment assemblePayin(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public Payment assemblePayout(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public SlimPayOrderItem assembleOrderItem(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public Mandate assembleMandate(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public Signatory assembleSignatory(PaymentRequest paymentRequest) {
        return null;
    }
}

