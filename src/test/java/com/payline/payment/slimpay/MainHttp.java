package com.payline.payment.slimpay;

import com.payline.payment.slimpay.bean.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.service.impl.RefundServiceImpl;
import com.payline.payment.slimpay.utils.DateUtils;
import com.payline.payment.slimpay.utils.TestUtils;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.service.RefundService;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;

public class MainHttp {

    public static void main( String[] args ) throws ParseException {
        RefundService refundService = new RefundServiceImpl();

        String orderRef = "REF-1568967431";
        String paymentId = "161d01d2-db7f-11e9-9e1b-000000000000";
        String paymentRef = "PAY-1568967431";
        String mandateRef = "RUM-1568967431";

        PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                .aPaymentResponseSuccessAdditionalData()
                .withOrderId("dontKnow")
                .withOrderReference( orderRef )
                .withMandateReference( mandateRef )
                .withPaymentReference( paymentRef )
                .withPaymentId( paymentId )
                .build();

        RefundRequest refundRequest = RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount( new Amount( new BigInteger("500", 10), Currency.getInstance("EUR")) )
                .withBuyer( TestUtils.createDefaultBuyer() )
                .withContractConfiguration( TestUtils.CONTRACT_CONFIGURATION )
                .withEnvironment( TestUtils.ENVIRONMENT )
                .withOrder( TestUtils.createOrder( orderRef ) )
                .withPartnerConfiguration( TestUtils.PARTNER_CONFIGURATION )
                .withPartnerTransactionId( paymentRef )
                .withTransactionAdditionalData( additionalData.toString() )
                .withTransactionId( paymentRef )
                .build();

        RefundResponse response = refundService.refundRequest( refundRequest );
        System.out.println("END");
    }

}
