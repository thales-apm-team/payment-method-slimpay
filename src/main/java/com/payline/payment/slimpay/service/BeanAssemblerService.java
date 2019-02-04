package com.payline.payment.slimpay.service;

import com.payline.payment.slimpay.bean.common.Mandate;
import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.common.Signatory;
import com.payline.payment.slimpay.bean.common.SlimPayOrderItem;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public interface BeanAssemblerService {
    Payment assemblePayin (PaymentRequest paymentRequest);

    Payment assemblePayout (PaymentRequest paymentRequest);

    SlimPayOrderItem assembleOrderItem (PaymentRequest paymentRequest);

    Mandate assembleMandate (PaymentRequest paymentRequest);

    Signatory assembleSignatory(PaymentRequest paymentRequest);
}
