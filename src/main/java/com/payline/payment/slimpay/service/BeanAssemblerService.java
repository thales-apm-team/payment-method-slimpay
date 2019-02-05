package com.payline.payment.slimpay.service;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.common.request.SlimpayOrderRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

public interface BeanAssemblerService {
    public Payment assemblePayin (PaymentRequest paymentRequest);

    public Payment assemblePayout (RefundRequest refundRequest);

    public SlimPayOrderItem assembleOrderItem (PaymentRequest paymentRequest);

    public SlimPayOrderItem assembleOrderItemMandate (PaymentRequest paymentRequest);

    public SlimPayOrderItem assembleOrderItemPayment (PaymentRequest paymentRequest);

    public Mandate assembleMandate (PaymentRequest paymentRequest);

    public Signatory assembleSignatory(PaymentRequest paymentRequest);

    public BillingAddress assembleBillingAddress(PaymentRequest paymentRequest);

    public SlimpayOrderRequest assembleSlimPayOrderRequest(PaymentRequest paymentRequest);

    }
