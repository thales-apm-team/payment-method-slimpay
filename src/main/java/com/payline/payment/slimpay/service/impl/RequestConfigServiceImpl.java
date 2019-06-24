package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.service.RequestConfigService;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.pmapi.bean.buyer.request.BuyerDetailsRequest;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.util.HashMap;
import java.util.Map;

public enum RequestConfigServiceImpl implements RequestConfigService {

    INSTANCE;

    protected enum PaylineParameterType {
        CONTRACT_CONFIGURATION_PARAMETER,
        PARTNER_CONFIGURATION_PARAMETER
    }

    /**
     * Map of all Contract or Partner parameters
     */
    protected static final Map<String, PaylineParameterType> PARAMETERS_MAP = new HashMap<>();

    static {
        // add contract config data
        PARAMETERS_MAP.put(SlimpayConstants.CREDITOR_REFERENCE_KEY, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.PAYMENT_PROCESSOR, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.FIRST_PAYMENT_SCHEME, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.MANDATE_PAYIN_SCHEME, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.SIGNATURE_APPROVAL_METHOD, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.APP_KEY, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.APP_SECRET, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);

        // add partner config data
        PARAMETERS_MAP.put(SlimpayConstants.API_URL_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.API_PROFILE_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.API_NS_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
    }

    RequestConfigServiceImpl() {
        // ras
    }

    @Override
    public String getParameterValue(ResetRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }


    @Override
    public String getParameterValue(RefundRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentFormLogoRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentFormConfigurationRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(NotifyTransactionStatusRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(TransactionStatusRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(ContractParametersCheckRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getAccountInfo(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(CaptureRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(BuyerDetailsRequest request, String key) throws InvalidDataException {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

}
