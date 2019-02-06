package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.common.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.service.BeanAssemblerService;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

import static com.payline.payment.slimpay.utils.PluginUtils.createStringAmount;
import static com.payline.payment.slimpay.utils.PluginUtils.getHonorificCode;
import static com.payline.payment.slimpay.utils.SlimpayConstants.*;

public class BeanAssemblerServiceImpl implements BeanAssemblerService {
    @Override
    public Payment assemblePayin(PaymentRequest paymentRequest) {
        return Payment.Builder.aPaymentBuilder()
                .withReference(paymentRequest.getOrder().getReference())
                .withScheme(paymentRequest.getContractConfiguration().getProperty(FIRST_PAYMENT_SCHEME).getValue())
                .withDirection("IN")
                .withAction("create")
                .withAmount(createStringAmount(paymentRequest.getAmount().getAmountInSmallestUnit(), paymentRequest.getAmount().getCurrency()))
                .withCurrency(paymentRequest.getAmount().getCurrency().toString())
                .withLabel(paymentRequest.getSoftDescriptor())
                .build();
    }

    @Override
    public Payment assemblePayout(RefundRequest paymentRequest) {
        return Payment.Builder.aPaymentBuilder()
                .withReference(paymentRequest.getOrder().getReference())
                .withScheme(paymentRequest.getContractConfiguration().getProperty(FIRST_PAYMENT_SCHEME).getValue())
                .withDirection("OUT")
                .withAmount(createStringAmount(paymentRequest.getAmount().getAmountInSmallestUnit(), paymentRequest.getAmount().getCurrency()))
                .withCurrency(paymentRequest.getAmount().getCurrency().toString())
                .withLabel(paymentRequest.getSoftDescriptor())
//                .withCreditor()
//                .withSubscriber()
//                .withCreditor()
                .build();
    }

    @Override
    public SlimPayOrderItem assembleOrderItem(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public SlimPayOrderItem assembleOrderItemMandate(PaymentRequest paymentRequest) {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("signMandate")
                .withMandate(assembleMandate(paymentRequest))
                .build();

    }

    @Override
    public SlimPayOrderItem assembleOrderItemPayment(PaymentRequest paymentRequest) {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("payment")
                .withPayin(assemblePayin(paymentRequest))
                .build();
    }

    @Override
    public Mandate assembleMandate(PaymentRequest paymentRequest) {
        return Mandate.Builder.aMandateBuilder()
                .withReference(paymentRequest.getTransactionId())
                .withStandard(MANDATE_STANDARD)
                .withAction("create")
                .withPaymentScheme(MANDATE_PAYIN_SCHEME)
                .withSignatory(assembleSignatory(paymentRequest))
                .build();
    }

    @Override
    public Signatory assembleSignatory(PaymentRequest paymentRequest) {
        Buyer buyer = paymentRequest.getBuyer();
        return Signatory.Builder.aSignatoryBuilder()
                .withfamilyName(buyer.getFullName().getFirstName())
                .withGivenName(buyer.getFullName().getLastName())
                .withHonorificPrefix(getHonorificCode(buyer.getFullName().getCivility()))
                .withBilingAddress(assembleBillingAddress(paymentRequest))
                .withEmail(buyer.getEmail())
                .withTelephone(buyer.getPhoneNumbers().get(Buyer.PhoneNumberType.CELLULAR))
                .build();
    }

    public BillingAddress assembleBillingAddress(PaymentRequest paymentRequest) {
        Buyer.Address address = paymentRequest.getBuyer().getAddressForType(Buyer.AddressType.BILLING);
        return BillingAddress.Builder.aBillingAddressBuilder()
                .withStreet1(address.getStreet1())
                .withStreet2(address.getStreet2())
                .withCity(address.getCity())
                .withCountry(address.getCountry())
                .withPostalCode(address.getZipCode())
                .build();
    }

    public SlimpayOrderRequest assembleSlimPayOrderRequest(PaymentRequest paymentRequest) {

        return SlimpayOrderRequest.Builder.aSlimPayOrderRequestBuilder()
                .withSubscriber(new SlimpayOrderRequest.Subscriber(paymentRequest.getBuyer().getCustomerIdentifier()))
                .withCreditor(new SlimpayOrderRequest.Creditor(paymentRequest.getContractConfiguration().getProperty(CREDITOR_REFERENCE_KEY).getValue()))
                .withSuccessUrl(paymentRequest.getEnvironment().getRedirectionReturnURL())
                .withFailureUrl(paymentRequest.getEnvironment().getRedirectionReturnURL())
                .withLocale(paymentRequest.getLocale().getCountry())
                .withStarted(true)
                .withItems(new SlimPayOrderItem[]{
                        assembleOrderItemMandate(paymentRequest),
                        assembleOrderItemPayment(paymentRequest)
                })
                .build();
    }


}

