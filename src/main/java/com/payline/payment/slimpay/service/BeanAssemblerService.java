package com.payline.payment.slimpay.service;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

public interface BeanAssemblerService {
    public Payment assemblePayin (PaymentRequest paymentRequest) throws InvalidDataException;

    public Payment assemblePayout (RefundRequest refundRequest) throws InvalidDataException;

    public SlimPayOrderItem assembleOrderItem (PaymentRequest paymentRequest);

    public SlimPayOrderItem assembleOrderItemMandate (PaymentRequest paymentRequest) throws InvalidDataException;

    public SlimPayOrderItem assembleOrderItemPayment (PaymentRequest paymentRequest) throws InvalidDataException;

    public Mandate assembleMandate (PaymentRequest paymentRequest) throws InvalidDataException;

    public Signatory assembleSignatory(PaymentRequest paymentRequest);

    public BillingAddress assembleBillingAddress(PaymentRequest paymentRequest);

    public SlimpayOrderRequest assembleSlimPayOrderRequest(PaymentRequest paymentRequest) throws InvalidDataException;

    }
