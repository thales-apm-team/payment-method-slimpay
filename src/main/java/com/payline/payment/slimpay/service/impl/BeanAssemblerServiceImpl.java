package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.*;
import com.payline.payment.slimpay.bean.common.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.bean.common.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.service.BeanAssemblerService;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;

import static com.payline.payment.slimpay.utils.PluginUtils.*;
import static com.payline.payment.slimpay.utils.SlimpayConstants.*;

public class BeanAssemblerServiceImpl implements BeanAssemblerService {
    private static final  String CREATE = "create";
    private static final  String PAYMENT = "payment";
    private static final  String SIGN_MANDATE = "signMandate";
    private static final  String IN = "IN";
    private static final  String OUT = "OUT";
    private static final  String PAYOUT_SCHEME = "SEPA.CREDIT_TRANSFER";
    //Type de prélèvement
    private static final  String RECURRENT = "RCUR";
    private static final  String PONCTUEL = "OOFF";


    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final BeanAssemblerServiceImpl INSTANCE = new BeanAssemblerServiceImpl();
    }

    /**
     * @return the singleton instance
     */
    public static BeanAssemblerServiceImpl getInstance() {
        return BeanAssemblerServiceImpl.SingletonHolder.INSTANCE;
    }

    /**
     * Create a Slimplay Payment with direction IN from a Payline PaymentRequest
     * @param paymentRequest
     * @return a a new  Payment
     */

    @Override
    public Payment assemblePayin(PaymentRequest paymentRequest) throws InvalidDataException {
        return Payment.Builder.aPaymentBuilder()
                .withReference(generatePaymentReference(paymentRequest.getOrder().getReference()))
                .withScheme(RequestConfigServiceImpl.INSTANCE.getParameterValue(paymentRequest, FIRST_PAYMENT_SCHEME))
                .withDirection(IN)
                .withAction(CREATE)
                .withAmount(createStringAmount(paymentRequest.getAmount().getAmountInSmallestUnit(), paymentRequest.getAmount().getCurrency()))
                .withCurrency(paymentRequest.getAmount().getCurrency().toString())
                .withLabel(paymentRequest.getSoftDescriptor())
                .build();
    }

    /**
     * Create a Slimplay Payment with direction OUT from a Payline PaymentRequest
     * @param refundRequest
     * @return a a new  Payment
     */
    @Override
    public Payment assemblePayout(RefundRequest refundRequest) throws InvalidDataException {

        //get MandateReference
        PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.fromJson(refundRequest.getTransactionAdditionalData());
        String mandateReference = additionalData.getMandateReference();

        return Payment.Builder.aPaymentBuilder()
                .withReference(generatePaymentReference(refundRequest.getOrder().getReference()))
                .withScheme(PAYOUT_SCHEME)
                .withDirection(OUT)
                .withAmount(createStringAmount(refundRequest.getAmount().getAmountInSmallestUnit(), refundRequest.getAmount().getCurrency()))
                .withCurrency(refundRequest.getAmount().getCurrency().toString())
                .withLabel(refundRequest.getSoftDescriptor())
                .withCorrelationId(refundRequest.getPartnerTransactionId())
//                .withSubscriber(new Subscriber(refundRequest.getBuyer().getCustomerIdentifier()))
                .withCreditor(new Creditor(RequestConfigServiceImpl.INSTANCE.getParameterValue(refundRequest, CREDITOR_REFERENCE_KEY)))
                .withMandate(Mandate.Builder.aMandateBuilder()
                        .withReference(mandateReference)
                        .build())
                .build();

    }

    @Override
    public SlimPayOrderItem assembleOrderItem(PaymentRequest paymentRequest) {
        return null;
    }

    /**
     * Create a SlimPayOrderItem with type signMandate and  a Mandate from a Payline PaymentRequest
     * @param paymentRequest
     * @return a a new  SlimPayOrderItem
     */
    @Override
    public SlimPayOrderItem assembleOrderItemMandate(PaymentRequest paymentRequest) throws InvalidDataException {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType(SIGN_MANDATE)
                .withMandate(assembleMandate(paymentRequest))
                .build();

    }
    /**
     * Create a SlimPayOrderItem with type payment and  a Payment (direction IN) from a Payline PaymentRequest
     * @param paymentRequest
     * @return a a new  SlimPayOrderItem
     */
    @Override
    public SlimPayOrderItem assembleOrderItemPayment(PaymentRequest paymentRequest) throws InvalidDataException {
        return SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType(PAYMENT)
                .withPayin(assemblePayin(paymentRequest))
                .build();
    }

    /**
     * Create a Mandate from a Payline PaymentRequest
     * @param paymentRequest
     * @return a new  Mandate
     */
    @Override
    public Mandate assembleMandate(PaymentRequest paymentRequest) throws InvalidDataException {
        return Mandate.Builder.aMandateBuilder()
                .withReference(generateMandateReference(paymentRequest.getTransactionId()))
                .withStandard(RequestConfigServiceImpl.INSTANCE.getParameterValue(paymentRequest, MANDATE_STANDARD_KEY))
                .withAction(CREATE)
                .withPaymentScheme(RequestConfigServiceImpl.INSTANCE.getParameterValue(paymentRequest, MANDATE_PAYIN_SCHEME))
                 .withCreateSequenceType(PONCTUEL)
                .withSequenceType(PONCTUEL)
                .withSignatory(assembleSignatory(paymentRequest))
                .build();
    }

    /**
     * Create a Signatory from a Payline PaymentRequest
     * @param paymentRequest
     * @return a new  Signatory
     */
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
    /**
     * Create a BillingAddress from a Payline PaymentRequest
     * @param paymentRequest
     * @return a new  BillingAddress
     */
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

    /**
     * Create a SlimpayOrderRequest from a Payline PaymentRequest
     * @param paymentRequest
     * @return a new  SlimpayOrderRequest
     */
    public SlimpayOrderRequest assembleSlimPayOrderRequest(PaymentRequest paymentRequest) throws InvalidDataException {
        return SlimpayOrderRequest.Builder.aSlimPayOrderRequestBuilder()
                .withReference(generateOrderReference(paymentRequest.getTransactionId()))
                .withSubscriber(new Subscriber(paymentRequest.getBuyer().getCustomerIdentifier()))
                .withCreditor(new Creditor(RequestConfigServiceImpl.INSTANCE.getParameterValue(paymentRequest, CREDITOR_REFERENCE_KEY)))
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
                .withSubscriber(new Subscriber(FOO))
                .withCreditor(new Creditor(request.getContractConfiguration().getProperty(CREDITOR_REFERENCE_KEY).getValue()))
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

