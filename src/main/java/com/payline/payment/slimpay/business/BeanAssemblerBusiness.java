package com.payline.payment.slimpay.business;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

public interface BeanAssemblerBusiness {
    /**
     * Create a Slimplay Payment with direction IN (subscriber to creditor) from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a a new  Payment
     */
    Payment assemblePayin (PaymentRequest paymentRequest) throws InvalidDataException;

    /**
     * Create a Slimplay Payment with direction OUT (  creditor to subscriber) from a Payline PaymentRequest
     *
     * @param refundRequest
     * @return a a new  Payment
     */
    Payment assemblePayout (RefundRequest refundRequest) throws InvalidDataException;

    /**
     * Create a SlimPayOrderItem with type signMandate and  a Mandate from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a a new  SlimPayOrderItem
     */
    SlimPayOrderItem assembleOrderItemMandate (PaymentRequest paymentRequest) throws InvalidDataException;

    /**
     * Create a SlimPayOrderItem with type payment and  a Payment (direction IN) from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a a new  SlimPayOrderItem
     */
    SlimPayOrderItem assembleOrderItemPayment (PaymentRequest paymentRequest) throws InvalidDataException;

    /**
     * Create a Mandate from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a new  Mandate
     */
    Mandate assembleMandate (PaymentRequest paymentRequest) throws InvalidDataException;

    /**
     * Create a Signatory from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a new  Signatory
     */
    Signatory assembleSignatory(PaymentRequest paymentRequest);

    /**
     * Create a BillingAddress from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a new  BillingAddress
     */
    BillingAddress assembleBillingAddress(PaymentRequest paymentRequest);

    /**
     * Create a SlimpayOrderRequest from a Payline PaymentRequest
     *
     * @param paymentRequest
     * @return a new  SlimpayOrderRequest
     */
    SlimpayOrderRequest assembleSlimPayOrderRequest(PaymentRequest paymentRequest) throws InvalidDataException;

    /**
     * Request used to test Simplay HTTP call
     *
     * @param request ContractParametersCheckRequest
     * @return SlimpayOrderRequest
     * @throws InvalidDataException
     */
    SlimpayOrderRequest assembleSlimPayOrderRequest(ContractParametersCheckRequest request) throws InvalidDataException;
}
