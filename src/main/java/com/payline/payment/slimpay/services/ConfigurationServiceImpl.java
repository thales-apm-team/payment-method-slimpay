package com.payline.payment.slimpay.services;

import com.payline.payment.slimpay.bean.SlimpayPaymentRequest;
import com.payline.payment.slimpay.bean.SlimpayPaymentResponse;
import com.payline.payment.slimpay.utils.*;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.PasswordParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    public static final String RELEASE_PROPERTIES_ERROR = "An error occurred reading the file: release.properties";

    private SlimpayHttpClient httpClient = new SlimpayHttpClient();
    private LocalizationService localization;
    private ReleaseInformation releaseInformation;

    public ConfigurationServiceImpl() {
        this.localization = LocalizationImpl.getInstance();
        this.releaseInformation = initReleaseInformation();
    }

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // Merchant name
        final InputParameter merchantName = new InputParameter();
        merchantName.setKey(SlimpayCardConstants.MERCHANT_NAME_KEY);
        merchantName.setLabel(localization.getSafeLocalizedString("contract.merchantName.label", locale));
        merchantName.setDescription(localization.getSafeLocalizedString("contract.merchantName.description", locale));
        merchantName.setRequired(true);

        parameters.add(merchantName);

        // Mid
        final InputParameter merchantId = new InputParameter();
        merchantId.setKey(SlimpayCardConstants.MERCHANT_ID_KEY);
        merchantId.setLabel(localization.getSafeLocalizedString("contract.merchantId.label", locale));
        merchantId.setDescription(localization.getSafeLocalizedString("contract.merchantId.description", locale));
        merchantId.setRequired(true);

        parameters.add(merchantId);

        // authorisation key
        final PasswordParameter authorisationKey = new PasswordParameter();
        authorisationKey.setKey(SlimpayCardConstants.AUTHORISATIONKEY_KEY);
        authorisationKey.setLabel(localization.getSafeLocalizedString("contract.authorisationKey.label", locale));
        authorisationKey.setDescription(localization.getSafeLocalizedString("contract.authorisationKey.description", locale));
        authorisationKey.setRequired(true);

        parameters.add(authorisationKey);

        //settlement key
        final PasswordParameter settlementKey = new PasswordParameter();
        settlementKey.setKey(SlimpayCardConstants.SETTLEMENT_KEY);
        settlementKey.setLabel(localization.getSafeLocalizedString("contract.settlementKey.label", locale));
        settlementKey.setDescription(localization.getSafeLocalizedString("contract.settlementKey.description", locale));
        settlementKey.setRequired(false);

        parameters.add(settlementKey);

        // age limit
        final InputParameter minAge = new InputParameter();
        minAge.setKey(SlimpayCardConstants.MINAGE_KEY);
        minAge.setLabel(localization.getSafeLocalizedString("contract.minAge.label", locale));
        minAge.setDescription(localization.getSafeLocalizedString("contract.minAge.description", locale));
        minAge.setRequired(false);

        parameters.add(minAge);

        // kyc level
        Map<String, String> kycLevelMap = new HashMap<>();
        kycLevelMap.put(SlimpayCardConstants.KYCLEVEL_SIMPLE, localization.getSafeLocalizedString("contract.kycLevel.simple", locale));
        kycLevelMap.put(SlimpayCardConstants.KYCLEVEL_FULL, localization.getSafeLocalizedString("contract.kycLevel.full", locale));

        final ListBoxParameter kycLevel = new ListBoxParameter();
        kycLevel.setKey(SlimpayCardConstants.KYCLEVEL_KEY);
        kycLevel.setLabel(localization.getSafeLocalizedString("contract.kycLevel.label", locale));
        kycLevel.setDescription(localization.getSafeLocalizedString("contract.kycLevel.description", locale));
        kycLevel.setList(kycLevelMap);
        kycLevel.setRequired(false);

        parameters.add(kycLevel);

        // country restriction
        final InputParameter countryRestriction = new InputParameter();
        countryRestriction.setKey(SlimpayCardConstants.COUNTRYRESTRICTION_KEY);
        countryRestriction.setLabel(localization.getSafeLocalizedString("contract.countryRestriction.label", locale));
        countryRestriction.setLabel(localization.getSafeLocalizedString("contract.countryRestriction.description", locale));
        countryRestriction.setRequired(false);

        parameters.add(countryRestriction);


        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = contractParametersCheckRequest.getLocale();

        // verify configuration fields
        String minAge = contractParametersCheckRequest.getContractConfiguration().getProperty(SlimpayCardConstants.MINAGE_KEY).getValue();
        String countryRestriction = contractParametersCheckRequest.getContractConfiguration().getProperty(SlimpayCardConstants.COUNTRYRESTRICTION_KEY).getValue();

        // verify fields
        try {
            DataChecker.verifyMinAge(minAge);
        } catch (BadFieldException e) {
            errors.put(e.getField(), localization.getSafeLocalizedString(e.getMessage(), locale));
        }

        try {
            DataChecker.verifyCountryRestriction(countryRestriction);
        } catch (BadFieldException e) {
            errors.put(e.getField(), localization.getSafeLocalizedString(e.getMessage(), locale));
        }

        // if there is some errors, stop the process and return them
        if (errors.size() > 0) {
            return errors;
        }

        try {
            // create a CheckRequest
            SlimpayPaymentRequest checkRequest = new SlimpayPaymentRequest(contractParametersCheckRequest);

            // do the request
            Boolean isSandbox = contractParametersCheckRequest.getEnvironment().isSandbox();
            SlimpayPaymentResponse response = httpClient.initiate(checkRequest, isSandbox);

            // check response object
            if (response.getCode() != null) {
                findErrors(response, errors);
            }

        } catch (IOException | URISyntaxException | InvalidRequestException e) {
            LOGGER.error("unable to check the connection: {}", e.getMessage(), e);
            errors.put(ContractParametersCheckRequest.GENERIC_ERROR, e.getMessage());
        }

        return errors;
    }

    /**
     * Initialize the ReleaseInformation
     * @return
     */
    public ReleaseInformation initReleaseInformation() {
        final Properties props = new Properties();
        try {
            props.load(ConfigurationServiceImpl.class.getClassLoader().getResourceAsStream("release.properties"));
        } catch (IOException e) {
            LOGGER.error(RELEASE_PROPERTIES_ERROR);
            throw new RuntimeException(RELEASE_PROPERTIES_ERROR, e);
        }

        final LocalDate date = LocalDate.parse(props.getProperty("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(date)
                .withVersion(props.getProperty("release.version"))
                .build();
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        if (null == releaseInformation) {
            throw new RuntimeException(RELEASE_PROPERTIES_ERROR);
        }
        return releaseInformation;
    }

    @Override
    public String getName(Locale locale) {
        return localization.getSafeLocalizedString("project.name", locale);
    }

    public void findErrors(SlimpayPaymentResponse message, Map<String, String> errors) {
        if (message.getCode() != null) {
            switch (message.getCode()) {
                case "invalid_api_key":
                    // bad authorisation key in header
                    errors.put(SlimpayCardConstants.AUTHORISATIONKEY_KEY, message.getMessage());
                    break;
                case "invalid_request_parameter":
                    // bad parameter, check field "param" to find it
                    if ("kyc_level".equals(message.getParam())) {
                        errors.put(SlimpayCardConstants.KYCLEVEL_KEY, message.getMessage());
                    } else if ("min_age".equals(message.getParam())) {
                        errors.put(SlimpayCardConstants.MINAGE_KEY, message.getMessage());
                    }
                    break;
                case "invalid_restriction":
                    // bad country restriction value
                    errors.put(SlimpayCardConstants.COUNTRYRESTRICTION_KEY, message.getMessage());
                    break;
                default:
                    errors.put(ContractParametersCheckRequest.GENERIC_ERROR, message.getMessage());
            }
        }
    }
}
