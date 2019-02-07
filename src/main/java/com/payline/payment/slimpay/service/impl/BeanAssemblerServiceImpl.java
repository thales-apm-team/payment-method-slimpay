package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.common.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.service.BeanAssemblerService;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

import static com.payline.payment.slimpay.utils.PluginUtils.createStringAmount;
import static com.payline.payment.slimpay.utils.PluginUtils.getHonorificCode;
import static com.payline.payment.slimpay.utils.SlimpayConstants.*;

// todo passer par RequestConfig pour acceder aux contract config
public class BeanAssemblerServiceImpl implements BeanAssemblerService {
    private final String CREATE = "create";
    private final String PAYMENT = "payment";
    private final String SIGN_MANDATE = "signMandate";
    private final String IN = "IN";
    private final String OUT = "OUT";

    @Override
    public Payment assemblePayin(PaymentRequest paymentRequest) {
        return Payment.Builder.aPaymentBuilder()
                .withReference(paymentRequest.getOrder().getReference())
                .withScheme(paymentRequest.getContractConfiguration().getProperty(FIRST_PAYMENT_SCHEME).getValue())
                .withDirection(IN)
                .withAction(CREATE)
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
                .withDirection(OUT)
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
                .withType(SIGN_MANDATE)
                .withMandate(assembleMandate(paymentRequest))
                .build();

    }

    @Override
    public SlimPayOrderItem assembleOrderItemPayment(PaymentRequest paymentRequest) {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType(PAYMENT)
                .withPayin(assemblePayin(paymentRequest))
                .build();
    }

    @Override
    public Mandate assembleMandate(PaymentRequest paymentRequest) {
        return Mandate.Builder.aMandateBuilder()
                .withReference(paymentRequest.getTransactionId())
                .withStandard(paymentRequest.getContractConfiguration().getProperty(MANDATE_STANDARD_KEY).getValue())
                .withAction(CREATE)
                .withPaymentScheme(paymentRequest.getContractConfiguration().getProperty(MANDATE_PAYIN_SCHEME).getValue())
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
                .withCancelUrl(paymentRequest.getEnvironment().getRedirectionCancelURL())
                .withLocale(paymentRequest.getLocale().getCountry())
                .withStarted(true)
                .withItems(new SlimPayOrderItem[]{
                        assembleOrderItemMandate(paymentRequest),
                        assembleOrderItemPayment(paymentRequest)
                })
                .build();
    }

    public SlimpayOrderRequest assembleSlimPayOrderRequest(ContractParametersCheckRequest request) throws InvalidDataException {
        final String FOO = "foo";
        final String STREET = "street";
        final String PREFIX = "Mr";
        final String COUNTRY = "US";
        final String PHONE = "+33601020304";
        final String MAIL = "foo@bar.com";
        final String REFERENCE = "123456789";

        BillingAddress address = BillingAddress.Builder.aBillingAddressBuilder()
                .withStreet1(STREET).withStreet2(STREET).withCity(FOO).withCountry(COUNTRY).withPostalCode(FOO).build();

        Signatory signatory = Signatory.Builder.aSignatoryBuilder()
                .withfamilyName(FOO).withGivenName(FOO).withHonorificPrefix(PREFIX).withBilingAddress(address).withEmail(MAIL).withTelephone(PHONE).build();

        Mandate mandate = Mandate.Builder.aMandateBuilder()
                .withReference(REFERENCE)
                .withStandard(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, MANDATE_STANDARD_KEY))
                .withAction(CREATE)
                .withPaymentScheme(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, MANDATE_PAYIN_SCHEME))
                .withSignatory(signatory)
                .build();

        return SlimpayOrderRequest.Builder.aSlimPayOrderRequestBuilder()
                .withSubscriber(new SlimpayOrderRequest.Subscriber(FOO))
                .withCreditor(new SlimpayOrderRequest.Creditor(request.getContractConfiguration().getProperty(CREDITOR_REFERENCE_KEY).getValue()))
                .withSuccessUrl(request.getEnvironment().getRedirectionReturnURL())
                .withFailureUrl(request.getEnvironment().getRedirectionReturnURL())
                .withCancelUrl(request.getEnvironment().getRedirectionCancelURL())
                .withLocale(request.getLocale().getCountry())
                .withStarted(true)
                .withItems(new SlimPayOrderItem[]{
                        SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder().withType(SIGN_MANDATE).withMandate(mandate).build()
                })
                .build();
    }

}

