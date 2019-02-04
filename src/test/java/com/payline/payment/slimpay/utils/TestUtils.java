package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.Buyer.Address;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import com.payline.pmapi.logger.LogManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Class with method to generate mock easier
 */
public class TestUtils {
    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);

    // FIXME
    private static final String SUCCESS_URL = AbstractPaymentIntegration.SUCCESS_URL;
    private static final String CANCEL_URL = "http://localhost/cancelurl.com/";
    private static final String NOTIFICATION_URL = "http://google.com/";
    private static final String MDP_IDENTIFIER = "paymentMethodIdentifier";


    private static final String SOFT_DESCRIPTOR = "softDescriptor";
    private static final String MERCHANT_REQUEST_ID = createMerchantRequestId();
    public static final String CONFIRM_AMOUNT = "40800";
    private static final String TRANSACTION_ID = "455454545415451198120";
    /**
     * ou
     * "141217" + Calendar.getInstance().getTimeInMillis()
     **/
    private static final String PARTNER_TRANSACTION_ID = "455454545415451198120";

    private static final Environment ENVIRONMENT = new Environment("https://succesurl.com/", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);
    private static final Locale LOCALE_FR = Locale.FRANCE;
    public static final String CURRENCY_EUR = "EUR";

    private static String TEST_PHONE_NUMBER = "0600000000";
    /**
     * ou
     * "+32" + RandomStringUtils.random(10, false, true)
     **/

    private static String TEST_EMAIL = "test.slimpay@yopmail.com";
    /**
     * ou
     * "test." + RandomStringUtils.random(5, true, false) + "@gmail.com"
     **/

    private static final Amount AMOUNT = new Amount(new BigInteger(CONFIRM_AMOUNT), Currency.getInstance(CURRENCY_EUR));
    private static final Order ORDER = Order.OrderBuilder.anOrder().withReference(TRANSACTION_ID).withAmount(AMOUNT).build();


    private static final Map<String, String> PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        // TODO
        put("KEY", "VALUE");
    }};


    private static final Map<String, String> SENSITIVE_PARTNER_CONFIGURATION_MAP = new HashMap<String, String>() {{
        // TODO
        put("KEY", "VALUE");
    }};

    public static final ContractConfiguration CONTRACT_CONFIGURATION = new ContractConfiguration(MDP_IDENTIFIER, new HashMap<String, ContractProperty>() {{
        // TODO
        put("KEY", new ContractProperty("VALUE"));
    }}
    );


    private static final Map<String, String> ACCOUNT_INFO = new HashMap<String, String>() {{
        // TODO
        put("KEY", "VALUE");
    }};

    public static final PartnerConfiguration PARTNER_CONFIGURATION = new PartnerConfiguration(PARTNER_CONFIGURATION_MAP, SENSITIVE_PARTNER_CONFIGURATION_MAP);

    public static Address createDefaultCompleteAddress() {
        return createCompleteAddress(RandomStringUtils.random(3, false, true)
                        + " rue " + RandomStringUtils.random(5, true, false),
                "residence " + RandomStringUtils.random(9
                        , true, false), "Marseille", "13015", "FR");
    }

    /**
     * Create a paymentRequest with default parameters.
     *
     * @return paymentRequest created
     */
    public static PaymentRequest createDefaultPaymentRequest() {
        final Order order = createOrder(TRANSACTION_ID);


        return PaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withLocale(LOCALE_FR)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(order)
                .withBuyer(createDefaultBuyer())
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
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


    public static RefundRequest createRefundRequest(String transactionId) {

        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(AMOUNT)
                .withOrder(createOrder(transactionId, AMOUNT))
                .withBuyer(createDefaultBuyer())
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withTransactionId(transactionId)
                .withPartnerTransactionId("toto")
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }

    public static RedirectionPaymentRequest createRedirectionPaymentRequest() {
        return RedirectionPaymentRequest.builder().build();

    }

    /**
     * Create a complete payment request used for Integration Tests
     *
     * @return PaymentRequest.Builder
     */

    public static PaymentRequest.Builder createCompletePaymentBuilder() {

        final Environment paylineEnvironment = new Environment(NOTIFICATION_URL, SUCCESS_URL, CANCEL_URL, true);

        final Order order = createOrder(TRANSACTION_ID);

        return PaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(paylineEnvironment)
                .withOrder(order)
                .withLocale(LOCALE_FR)
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withPaymentFormContext(createDefaultPaymentFormContext(TEST_PHONE_NUMBER))
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer());
    }

    //Cree une redirection payment par defaut
    public static RedirectionPaymentRequest createCompleteRedirectionPaymentBuilder() {

        final Order order = createOrder(TRANSACTION_ID);


        Map<String, String> requestData = new HashMap<>();
        // TODO;


        final RequestContext requestContext = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestData)
                .build();
        return RedirectionPaymentRequest.builder()
                .withAmount(AMOUNT)
                .withBrowser(new Browser("", LOCALE_FR))
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withOrder(order)
                .withLocale(LOCALE_FR)
                .withTransactionId(TRANSACTION_ID)
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withPaymentFormContext(createDefaultPaymentFormContext(TEST_PHONE_NUMBER))
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withLocale(LOCALE_FR)
                .withBuyer(createDefaultBuyer())
                //propre a la redirectionPayment
//                .withPaymentFormContext()
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
        return new Buyer.FullName(RandomStringUtils.random(7, true, false), RandomStringUtils.random(10, true, false), "4");
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
                .withContractConfiguration(CONTRACT_CONFIGURATION)
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
                .withCustomerIdentifier("subscriber12")
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


    public static TransactionStatusRequest createDefaultTransactionStatusRequest() {
        return TransactionStatusRequest.TransactionStatusRequestBuilder
                .aNotificationRequest()
                .withTransactionId(TRANSACTION_ID)
                .withAmount(AMOUNT)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withEnvironment(ENVIRONMENT)
                .withOrder(createOrder(TRANSACTION_ID))
                .withBuyer(createDefaultBuyer())
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .build();
    }

    public static RefundRequest createDefaultRefundRequest() {
        final Order order = createOrder(TRANSACTION_ID);


        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(AMOUNT)
                .withContractConfiguration(CONTRACT_CONFIGURATION)
                .withOrder(order)
                .withBuyer(createDefaultBuyer())
                .withSoftDescriptor(SOFT_DESCRIPTOR)
                .withEnvironment(ENVIRONMENT)
                .withPartnerConfiguration(PARTNER_CONFIGURATION)
                .withPartnerTransactionId(PARTNER_TRANSACTION_ID)
                .withTransactionId(TRANSACTION_ID)
                .build();
    }
}
