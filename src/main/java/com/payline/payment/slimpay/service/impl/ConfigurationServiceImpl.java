package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.business.impl.BeanAssemblerBusinessImpl;
import com.payline.payment.slimpay.business.impl.RequestConfigBusinessImpl;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.PluginUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.i18n.I18nService;
import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.slimpay.utils.properties.service.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.payline.payment.slimpay.utils.SlimpayConstants.*;

public class ConfigurationServiceImpl implements ConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private static final String FORBIDDEN = "403";
    private static final String OTP = "otp";
    private static final String SLIMPAY = "Slimpay";
    private static final String SDD_CORE = "SEPA.DIRECT_DEBIT.CORE";
    private static final String UNAUTHORIZED = "401";

    private I18nService i18n = I18nService.getInstance();
    private RequestConfigBusinessImpl requestConfigService = RequestConfigBusinessImpl.getInstance();
    private BeanAssemblerBusinessImpl beanAssemblerBusiness = BeanAssemblerBusinessImpl.getInstance();
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();


    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // creditorReference
        final InputParameter creditorReference = new InputParameter();
        creditorReference.setKey(CREDITOR_REFERENCE_KEY);
        creditorReference.setLabel(this.i18n.getMessage(CREDITOR_REFERENCE_KEY_LABEL, locale));
        creditorReference.setDescription(this.i18n.getMessage(CREDITOR_REFERENCE_KEY_LABEL, locale));
        creditorReference.setRequired(true);
        parameters.add(creditorReference);

        // paymentProcessor
        final ListBoxParameter paymentProcessor = new ListBoxParameter();
        paymentProcessor.setKey(PAYMENT_PROCESSOR);
        paymentProcessor.setLabel(this.i18n.getMessage(PAYMENT_PROCESSOR_LABEL, locale));
        paymentProcessor.setDescription(this.i18n.getMessage(PAYMENT_PROCESSOR_DESCRIPTION, locale));
        paymentProcessor.setRequired(true);
        final LinkedHashMap<String, String> processors = new LinkedHashMap<>();
        processors.put(SLIMPAY, SLIMPAY);
        paymentProcessor.setList(processors);
        paymentProcessor.setValue(SLIMPAY);
        parameters.add(paymentProcessor);

        //paymentScheme
        final ListBoxParameter paymentScheme = new ListBoxParameter();
        paymentScheme.setKey(FIRST_PAYMENT_SCHEME);
        paymentScheme.setLabel(this.i18n.getMessage(FIRST_PAYMENT_SCHEME_LABEL, locale));
        paymentScheme.setDescription(this.i18n.getMessage(FIRST_PAYMENT_SCHEME_DESCRIPTION, locale));
        paymentScheme.setRequired(true);
        final LinkedHashMap<String, String> paymentSchemes = new LinkedHashMap<>();
        paymentSchemes.put(SDD_CORE, SDD_CORE);
        paymentScheme.setList(paymentSchemes);
        paymentScheme.setValue(SDD_CORE);
        parameters.add(paymentScheme);

        // mandateScheme
        final ListBoxParameter mandateScheme = new ListBoxParameter();
        mandateScheme.setKey(MANDATE_PAYIN_SCHEME);
        mandateScheme.setLabel(this.i18n.getMessage(MANDATE_PAYIN_SCHEME_LABEL, locale));
        mandateScheme.setDescription(this.i18n.getMessage(MANDATE_PAYIN_SCHEME_DESCRIPTION, locale));
        mandateScheme.setRequired(true);
        final LinkedHashMap<String, String> mandateSchemes = new LinkedHashMap<>();
        mandateSchemes.put(SDD_CORE, SDD_CORE);
        mandateScheme.setList(mandateSchemes);
        mandateScheme.setValue(SDD_CORE);
        parameters.add(mandateScheme);

        // signatureApproval method
        final ListBoxParameter signatureApproval = new ListBoxParameter();
        signatureApproval.setKey(SIGNATURE_APPROVAL_METHOD);
        signatureApproval.setLabel(this.i18n.getMessage(SIGNATURE_APPROVAL_METHOD_LABEL, locale));
        signatureApproval.setRequired(true);
        final LinkedHashMap<String, String> signatures = new LinkedHashMap<>();
        signatures.put(OTP, OTP);
        signatureApproval.setList(signatures);
        signatureApproval.setValue(OTP);
        parameters.add(signatureApproval);

        return parameters;

    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        Locale locale = contractParametersCheckRequest.getLocale();
        final Map<String, String> errors = new HashMap<>();

        try {
            // verify every mandatory fields
            // CreditoReference
            String creditoReference = requestConfigService.getParameterValue(contractParametersCheckRequest, CREDITOR_REFERENCE_KEY);
            if (PluginUtils.isEmpty(creditoReference)) {
                errors.put(CREDITOR_REFERENCE_KEY, this.i18n.getMessage(CREDITOR_REFERENCE_KEY_MESSAGE_ERROR, locale));
            }

            // paymentScheme
            final String paymentScheme = requestConfigService.getParameterValue(contractParametersCheckRequest, FIRST_PAYMENT_SCHEME);
            if (PluginUtils.isEmpty(paymentScheme)) {
                errors.put(FIRST_PAYMENT_SCHEME, this.i18n.getMessage(FIRST_PAYMENT_MESSAGE_ERROR, locale));
            }

            // mandateScheme
            final String mandateScheme = requestConfigService.getParameterValue(contractParametersCheckRequest, MANDATE_PAYIN_SCHEME);
            if (PluginUtils.isEmpty(mandateScheme)) {
                errors.put(MANDATE_PAYIN_SCHEME, this.i18n.getMessage(MANDATE_PAYIN_MESSAGE_ERROR, locale));
            }
            // signatureApproval
            final String signatureApproval = requestConfigService.getParameterValue(contractParametersCheckRequest, SIGNATURE_APPROVAL_METHOD);
            if (PluginUtils.isEmpty(signatureApproval)) {
                errors.put(SIGNATURE_APPROVAL_METHOD, this.i18n.getMessage(SIGNATURE_APPROVAL_METHOD_MESSAGE_ERROR, locale));
            }

            // paymentProcessor
            final String paymentProcessor = requestConfigService.getParameterValue(contractParametersCheckRequest, PAYMENT_PROCESSOR);
            if (PluginUtils.isEmpty(paymentProcessor)) {
                errors.put(PAYMENT_PROCESSOR, this.i18n.getMessage(PAYMENT_PROCESSOR_MESSAGE_ERROR, locale));
            }

            // appKey
            final String appName = requestConfigService.getParameterValue(contractParametersCheckRequest, APP_KEY);
            if (PluginUtils.isEmpty(appName)) {
                errors.put(APP_KEY, this.i18n.getMessage(APP_KEY_MESSAGE_ERROR, locale));
            }

            // appSecret
            final String appSecret = requestConfigService.getParameterValue(contractParametersCheckRequest, APP_SECRET);
            if (PluginUtils.isEmpty(appSecret)) {
                errors.put(APP_SECRET, this.i18n.getMessage(APP_SECRET_MESSAGE_ERROR, locale));
            }

            if (errors.size() == 0) {
                // test a create order and call the API
                SlimpayOrderRequest request = beanAssemblerBusiness.assembleSlimPayOrderRequest(contractParametersCheckRequest);
                httpClient.testConnection(contractParametersCheckRequest.getPartnerConfiguration(), request.toJsonBody());
            }

        } catch (PluginTechnicalException e) {
            //log error
            LOGGER.error("Error while calling the plugin {}", e);
            String errorCode = e.getMessage();
            //if client side error, add it to error array
            if (errorCode.contains(UNAUTHORIZED) || errorCode.contains(FORBIDDEN)) {
                //if auth failed
                errors.put(APP_KEY, this.i18n.getMessage(API_TEST_MESSAGE_ERROR, locale));
                return errors;
            }

        }


        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        ReleaseProperties releaseProperties = ReleaseProperties.getInstance();

        LocalDate date = LocalDate.parse(releaseProperties.get(ConfigurationConstants.RELEASE_DATE),
                DateTimeFormatter.ofPattern(ConfigurationConstants.RELEASE_DATE_FORMAT));

        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(date)
                .withVersion(releaseProperties.get(ConfigurationConstants.RELEASE_VERSION))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return this.i18n.getMessage(ConfigurationConstants.PAYMENT_METHOD_NAME, locale);
    }
}
