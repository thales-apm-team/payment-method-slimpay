package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.payline.payment.slimpay.utils.TestUtils.createCompletePaymentBuilder;
import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;

public class BeanAssemblerServiceTest {

    private BeanAssemblerServiceImpl assemblerService = new BeanAssemblerServiceImpl();
    private PaymentRequest paymentRequest = createCompletePaymentBuilder().build();
    private RefundRequest refundRequest = createRefundRequest("request","400");


    @Test
    public void assemblePayin() throws InvalidDataException {
        Payment payin = assemblerService.assemblePayin(paymentRequest);
        String jsonPayin = payin.toString();
        Assertions.assertTrue(jsonPayin.contains("amount"));
        Assertions.assertTrue(jsonPayin.contains("currency"));
        Assertions.assertTrue(jsonPayin.contains("reference"));
        Assertions.assertTrue(jsonPayin.contains("direction"));

    }

    @Test
    public void assemblePayout() throws InvalidDataException {
        Payment payin = assemblerService.assemblePayout(refundRequest);
        String jsonPayin = payin.toString();
        Assertions.assertTrue(jsonPayin.contains("amount"));
        Assertions.assertTrue(jsonPayin.contains("currency"));
        Assertions.assertTrue(jsonPayin.contains("reference"));
        Assertions.assertTrue(jsonPayin.contains("direction"));

    }

    @Test
    public void assembleOrderItem() {
    }

    @Test
    public void assembleOrderItemMandate() throws InvalidDataException {
        SlimPayOrderItem orderItemMandate = assemblerService.assembleOrderItemMandate(paymentRequest);
        String jsonMandate = orderItemMandate.toString();
        System.out.println(jsonMandate);
        Assertions.assertTrue(jsonMandate.contains("type"));
        Assertions.assertTrue(jsonMandate.contains("mandate"));
    }

    @Test
    public void assembleOrderItemPayment() throws InvalidDataException {
        SlimPayOrderItem orderItemPayment = assemblerService.assembleOrderItemPayment(paymentRequest);
        String jsonOrderItemPayment = orderItemPayment.toString();
        System.out.println(jsonOrderItemPayment);
        Assertions.assertTrue(jsonOrderItemPayment.contains("type"));
        Assertions.assertTrue(jsonOrderItemPayment.contains("payin"));
    }

    @Test
    public void assembleMandate() throws InvalidDataException {
        Mandate mandate = assemblerService.assembleMandate(paymentRequest);
        String jsonMandate = mandate.toString();
        Assertions.assertTrue(jsonMandate.contains("reference"));
        Assertions.assertTrue(jsonMandate.contains("signatory"));
        Assertions.assertTrue(jsonMandate.contains("paymentScheme"));
        Assertions.assertTrue(jsonMandate.contains("action"));
        Assertions.assertTrue(jsonMandate.contains("reference"));
        Assertions.assertTrue(jsonMandate.contains("createSequenceType"));
    }

    @Test
    public void assembleSignatory() {
        Signatory signatory = assemblerService.assembleSignatory(paymentRequest);
        String jsonSignatory = signatory.toString();
        Assertions.assertTrue(jsonSignatory.contains("honorificPrefix"));
        Assertions.assertTrue(jsonSignatory.contains("familyName"));
        Assertions.assertTrue(jsonSignatory.contains("givenName"));
        Assertions.assertTrue(jsonSignatory.contains("email"));
        Assertions.assertTrue(jsonSignatory.contains("telephone"));
        Assertions.assertTrue(jsonSignatory.contains("billingAddress"));

    }

    @Test
    public void assembleBillingAddress() {
        BillingAddress address = assemblerService.assembleBillingAddress(paymentRequest);
        String jsonAddress = address.toString();
        Assertions.assertTrue(jsonAddress.contains("street1"));
        Assertions.assertTrue(jsonAddress.contains("street2"));
        Assertions.assertTrue(jsonAddress.contains("city"));
        Assertions.assertTrue(jsonAddress.contains("postalCode"));
        Assertions.assertTrue(jsonAddress.contains("country"));
    }

    @Test
    public void assembleSlimpayOrderRequest() throws InvalidDataException {
        SlimpayOrderRequest spOrderRequest = assemblerService.assembleSlimPayOrderRequest(paymentRequest);
        String jsonOrderRequest = spOrderRequest.toString();
        System.out.println(jsonOrderRequest);
        Assertions.assertTrue(jsonOrderRequest.contains("reference"));
        Assertions.assertTrue(jsonOrderRequest.contains("creditor"));
        Assertions.assertTrue(jsonOrderRequest.contains("subscriber"));
        Assertions.assertTrue(jsonOrderRequest.contains("items"));
        Assertions.assertTrue(jsonOrderRequest.contains("paymentScheme"));

    }
    }
