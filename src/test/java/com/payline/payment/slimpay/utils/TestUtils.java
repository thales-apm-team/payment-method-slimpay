package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.Buyer.Address;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.logger.LogManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.payline.payment.slimpay.utils.SlimpayConstants.*;


/**
 * Class with method to generate mock easier
 */
public class TestUtils {
    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);

    public static final String SUCCESS_URL = "https://succesurl.com/";
    public static final String CANCEL_URL = "http://localhost/cancelurl.com/";
    public static final String NOTIFICATION_URL = "http://google.com/";
    private static final String MDP_IDENTIFIER = "Slimpay";


    private static final String SOFT_DESCRIPTOR = "softDescriptor";
    private static final String ORDER_REFERENCE = "REF-" + Calendar.getInstance().getTimeInMillis();;
    private static final String CONFIRM_AMOUNT = "40800";
    private static final String TRANSACTION_ID = "HDEV-" + Calendar.getInstance().getTimeInMillis();
    private static final String CUSTOMER_ID = "Client2";
    private static final String ADDITIONAL_DATA = "{mandateReference: \""+TRANSACTION_ID+"\",mandateId: null," +
            "orderReference: \""+TRANSACTION_ID+"\",orderId: \"Transaction01\"," +
            "paymentReference: \""+TRANSACTION_ID+"\",paymentId: \"c6a29177-35e0-11e9-ad8f-000000000000\"}";

    /**
     * ou
     * "141217" + Calendar.getInstance().getTimeInMillis()
     **/
    private static final String PARTNER_TRANSACTION_ID = "455454545415451198120";

    public static final Environment ENVIRONMENT = new Environment("https://notification.com/", "http://succesurl.com", "http://redirectionCancelURL.com", true);
    private static final Locale LOCALE_FR = Locale.FRANCE;
    public static final String CURRENCY_EUR = "EUR";
    public static final Amount AMOUNT = new Amount(new BigInteger(CONFIRM_AMOUNT), Currency.getInstance(CURRENCY_EUR));

    private static String TEST_PHONE_NUMBER = "+33600000000";
    /**
     * ou
     * "+32" + RandomStringUtils.random(10, false, true)
     **/

    private static String TEST_EMAIL = "test.slimpay@yopmail.com";
    /**
     * ou
     * "test." + RandomStringUtils.random(5, true, false) + "@gmail.com"
     **/


    public static final Map<String, String> PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        put(API_URL_KEY, "https://api.preprod.slimpay.com");
        put(API_PROFILE_KEY, "https://api.slimpay.net/alps/v1");
        put(API_NS_KEY, "https://api.slimpay.net/alps");
        put(APP_KEY, "monextreferral01");
    }};


    public static final Map<String, String> SENSITIVE_PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        put(APP_SECRET, "4KcfEYKBKL4woXt1rkD29c7bRJwehCCbE0szzhD7");
    }};

    public static final ContractConfiguration CONTRACT_CONFIGURATION = new ContractConfiguration(MDP_IDENTIFIER, new HashMap<String, ContractProperty>() {{
        put(CREDITOR_REFERENCE_KEY, new ContractProperty("paylinemerchanttest2"));
        put(FIRST_PAYMENT_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
        put(MANDATE_PAYIN_SCHEME, new ContractProperty("SEPA.DIRECT_DEBIT.CORE"));
        put(MANDATE_STANDARD_KEY, new ContractProperty("SEPA"));
        put(SIGNATURE_APPROVAL_METHOD, new ContractProperty("otp"));
        put(PAYMENT_PROCESSOR, new ContractProperty("slimpay"));
    }}
    );
    public static final ContractConfiguration EMPTY_CONTRACT_CONFIGURATION = new ContractConfiguration(MDP_IDENTIFIER, new HashMap<String, ContractProperty>() {{

    }}
    );

    private static final Map<String, String> ACCOUNT_INFO = new HashMap<String, String>() {{
        put(CREDITOR_REFERENCE_KEY, "paylinemerchanttest1");
        put(FIRST_PAYMENT_SCHEME, "SEPA.DIRECT_DEBIT.CORE");
        put(MANDATE_PAYIN_SCHEME, ("SEPA.DIRECT_DEBIT.CORE"));
        put(SIGNATURE_APPROVAL_METHOD, "otp");
        put(MANDATE_STANDARD_KEY, "SEPA");
        put(PAYMENT_PROCESSOR, "slimpay");
    }};

    public static final PartnerConfiguration PARTNER_CONFIGURATION = new PartnerConfiguration(PARTNER_CONFIGURATION_MAP, SENSITIVE_PARTNER_CONFIGURATION_MAP);

    public static Address createRandomAddress() {
        return createCompleteAddress(RandomStringUtils.random(3, false, true)
                        + " rue " + RandomStringUtils.random(5, true, false),
                "residence " + RandomStringUtils.random(9
                        , true, false), "Marseille", "13015", "FR");
    }

    public static Address createDefaultCompleteAddress() {
        return createCompleteAddress("141 rue de la Paix",
                "residence MNPL", "Toulouse", "33000", "FR");
    }

    /**
     * Create a paymentRequest with default parameters.
     *
     * @return paymentRequest created
     */
    public static PaymentRequest createDefaultPaymentRequest() {
        return PaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withLocale(LOCALE_FR)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(createDefaultOrder())
                .withBuyer(createDefaultBuyer())
                .withDifferedActionDate(createDifferedDate())
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }

    public static PaymentRequest createBadPaymentRequest() {
        final Order order = createOrder("DEV-1549623741449");


        return PaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withLocale(LOCALE_FR)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(order)
                .withBuyer(createDefaultBuyer())
                .withTransactionId("DEV-1549623741449")
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withDifferedActionDate(createDifferedDate())
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }


    /**
     * Create a default form context for Unit Test and IT Test
     *
     * @return PaymentFormContext which contain a mobile phone number and a iban
     */
    public static PaymentFormContext createDefaultPaymentFormContext(String phoneNumber) {


        return PaymentFormContext.PaymentFormContextBuilder
                .aPaymentFormContext()
                .withPaymentFormParameter(PARTNER_CONFIGURATION_MAP)
                .withSensitivePaymentFormParameter(SENSITIVE_PARTNER_CONFIGURATION_MAP)
                .build();

    }


    public static RefundRequest createRefundRequest(String transactionId, String amount) {

        Amount amountRefunded = new Amount(new BigInteger(amount), Currency.getInstance(CURRENCY_EUR));
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(amountRefunded)
                .withOrder(createOrder(transactionId, AMOUNT))
                .withBuyer(createDefaultBuyer())
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withTransactionId(transactionId)
                .withPartnerTransactionId(TRANSACTION_ID)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withTransactionAdditionalData(ADDITIONAL_DATA)
                .build();
    }

    public static ResetRequest createResetRequest(String transactionId){
        return ResetRequest.ResetRequestBuilder.aResetRequest()
                .withAmount(AMOUNT)
                .withBuyer(createDefaultBuyer())
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withOrder(createOrder(transactionId, AMOUNT))
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withPartnerTransactionId(TRANSACTION_ID)
                .withTransactionAdditionalData(ADDITIONAL_DATA)
                .withTransactionId(transactionId)
                .build();
    }


    public static RedirectionPaymentRequest createRedirectionPaymentRequest(String transactionId) {
        return RedirectionPaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withLocale(LOCALE_FR)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withBuyer(createDefaultBuyer())
                .withTransactionId(transactionId)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withEnvironment(ENVIRONMENT)
                .withOrder(createDefaultOrder())
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();


    }

    /**
     * Create a complete payment request used for Integration Tests
     *
     * @return PaymentRequest.Builder
     */

    public static PaymentRequest.Builder createCompletePaymentBuilder() {
        final Environment paylineEnvironment = new Environment(NOTIFICATION_URL, SUCCESS_URL, CANCEL_URL, true);

        return PaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(paylineEnvironment)
                .withOrder(createDefaultOrder())
                .withLocale(LOCALE_FR)
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withDifferedActionDate(createDifferedDate())
                .withPaymentFormContext(createDefaultPaymentFormContext(TEST_PHONE_NUMBER))
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer());
    }

    //Cree une redirection payment par defaut
    public static RedirectionPaymentRequest createCompleteRedirectionPaymentBuilder() {

        Map<String, String> requestData = new HashMap<>();
        requestData.put(SlimpayConstants.CREDITOR_REFERENCE_KEY, "paylinemerchanttest1");
        requestData.put(SlimpayConstants.ORDER_REFERENCE, TRANSACTION_ID);
        requestData.put(SlimpayConstants.ORDER_ID, "ff4ea3a6-303e-11e9-9d34-000000000000");

        final RequestContext requestContext = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestData)
                .build();
        return RedirectionPaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withOrder(createDefaultOrder())
                .withLocale(LOCALE_FR)
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withPaymentFormContext(createDefaultPaymentFormContext(TEST_PHONE_NUMBER))
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer())
                .withRequestContext(requestContext)
                .build();


    }


    public static String createMerchantRequestId() {
        return "131217" + Calendar.getInstance().getTimeInMillis();
    }


    public static Map<Buyer.AddressType, Address> createAddresses(Address address) {
        Map<Buyer.AddressType, Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.DELIVERY, address);
        addresses.put(Buyer.AddressType.BILLING, address);

        return addresses;
    }

    public static Map<Buyer.AddressType, Address> createDefaultAddresses() {
        Address address = createDefaultCompleteAddress();
        return createAddresses(address);
    }

    public static Amount createAmount(String currency) {
        return new Amount(BigInteger.TEN, Currency.getInstance(currency));
    }


    public static Order createDefaultOrder(){
        return createOrder( ORDER_REFERENCE );
    }

    public static Order createOrder(String transactionID) {
        List<Order.OrderItem> orderItems = new ArrayList<>();
        orderItems.add(createOrderItem("item1", createAmount(CURRENCY_EUR)));
        orderItems.add(createOrderItem("item2", createAmount(CURRENCY_EUR)));
        return Order.OrderBuilder.anOrder()
                .withReference(transactionID)
                .withAmount(AMOUNT)
                .withDate(new Date())
                .withItems(orderItems)
                .withDeliveryMode("1")
                .withDeliveryTime("2")
                .withExpectedDeliveryDate(new Date())
                .build();
    }

    public static Order.OrderItem createOrderItem(String reference, Amount amount) {
        return Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(amount)
                .withQuantity(4L)
                .withCategory("20001")
                .withComment("some label")
                .withBrand("someBrand")
                .withReference(reference)
                .withTaxRatePercentage(BigDecimal.TEN)
                .build();
    }

    public static Order createOrder(String transactionID, Amount amount) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).withAmount(amount).build();
    }


    public static Buyer.FullName createFullName() {
        return new Buyer.FullName("Jumper", "Johnny", "4");
    }

    public static Map<Buyer.PhoneNumberType, String> createDefaultPhoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.HOME, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.UNDEFINED, TEST_PHONE_NUMBER);
        phoneNumbers.put(Buyer.PhoneNumberType.WORK, TEST_PHONE_NUMBER);

        return phoneNumbers;
    }


    public static ContractParametersCheckRequest createContractParametersCheckRequest() {

        return ContractParametersCheckRequest
                .CheckRequestBuilder
                .aCheckRequest()
                .withContractConfiguration(EMPTY_CONTRACT_CONFIGURATION)
                .withAccountInfo(ACCOUNT_INFO)
                .withEnvironment(ENVIRONMENT)
                .withLocale(LOCALE_FR)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }


    public static Address createCompleteAddress(String street, String street2, String city, String zip, String country) {
        return Address.AddressBuilder.anAddress()
                .withStreet1(street)
                .withStreet2(street2)
                .withCity(city)
                .withZipCode(zip)
                .withCountry(country)
                .withFullName(createFullName())
                .build();
    }


    public static Buyer createBuyer(Map<Buyer.PhoneNumberType, String> phoneNumbers, Map<Buyer.AddressType, Address> addresses, Buyer.FullName fullName) {
        return Buyer.BuyerBuilder.aBuyer()
                .withEmail(TEST_EMAIL)
                .withPhoneNumbers(phoneNumbers)
                .withAddresses(addresses)
                .withFullName(fullName)
                .withCustomerIdentifier(CUSTOMER_ID)
                .withExtendedData(createDefaultExtendedData())
                .withBirthday(getBirthdayDate())
                .withLegalStatus(Buyer.LegalStatus.PERSON)
                .build();
    }


    private static Date getBirthdayDate() {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse("04/05/1981");
        } catch (ParseException e) {
            LOGGER.error("parsing de la date de naissance impossible", e);
            return null;
        }
    }


    public static Map<String, String> createDefaultExtendedData() {

        return new HashMap<String, String>();

    }

    public static Buyer createDefaultBuyer() {
        return createBuyer(createDefaultPhoneNumbers(), createDefaultAddresses(), createFullName());
    }


    public static PaymentFormConfigurationRequest createDefaultPaymentFormConfigurationRequest() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer())
                .withAmount(AMOUNT)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(createOrder("007"))
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }


    public static TransactionStatusRequest createDefaultTransactionStatusRequest(String transactionId) {
        return TransactionStatusRequest.TransactionStatusRequestBuilder
                .aNotificationRequest()
                .withTransactionId(transactionId)
                .withAmount(AMOUNT)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withOrder(createDefaultOrder())
                .withBuyer(createDefaultBuyer())
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }


    public static NotifyTransactionStatusRequest createDefaultNotifyTransactionStatusRequest(String transactionId) {
        return NotifyTransactionStatusRequest.NotifyTransactionStatusRequestBuilder
                .aNotifyTransactionStatusRequest()
                .withAmount(AMOUNT)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withPartnerTransactionId(transactionId)
                .build();
    }

    public static TransactionStatusRequest createDefaultTransactionStatusRequest() {
        return createDefaultTransactionStatusRequest(TRANSACTION_ID);
    }

    public static RefundRequest createDefaultRefundRequest() {
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(AMOUNT)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(createDefaultOrder())
                .withBuyer(createDefaultBuyer())
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withPartnerTransactionId(PARTNER_TRANSACTION_ID)
                .withTransactionId(TRANSACTION_ID)
                .withTransactionAdditionalData(ADDITIONAL_DATA)
                .build();
    }


    private static Date createDifferedDate(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar c = Calendar.getInstance();
        c.setTime( new Date());
        c.add(Calendar.DATE, 4);
        df.format( c.getTime() );

        return c.getTime();
    }

}
