package com.payline.payment.slimpay.business.impl;

import com.payline.payment.slimpay.business.RequestConfigBusiness;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.payment.slimpay.utils.properties.service.ConfigProperties;
import com.payline.pmapi.bean.buyer.request.BuyerDetailsRequest;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestConfigBusinessImpl implements RequestConfigBusiness {

    protected enum PaylineParameterType {
        CONTRACT_CONFIGURATION_PARAMETER,
        PARTNER_CONFIGURATION_PARAMETER;
    }

    /**
     * Map of all Contract or Partner parameters
     */
    static final Map<String, PaylineParameterType> PARAMETERS_MAP = new HashMap<>();

    static {
        // add contract config data
        PARAMETERS_MAP.put(SlimpayConstants.CREDITOR_REFERENCE_KEY, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.PAYMENT_PROCESSOR, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.FIRST_PAYMENT_SCHEME, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.MANDATE_PAYIN_SCHEME, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.SIGNATURE_APPROVAL_METHOD, PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);

        // add partner config data
        PARAMETERS_MAP.put(SlimpayConstants.API_URL_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.API_PROFILE_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.API_NS_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.APP_KEY, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
        PARAMETERS_MAP.put(SlimpayConstants.APP_SECRET, PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER);
    }

    RequestConfigBusinessImpl() {
    }

    private static class Holder {
        private static final RequestConfigBusinessImpl instance = new RequestConfigBusinessImpl();
    }

    public static RequestConfigBusinessImpl getInstance(){
        return Holder.instance;
    }

    @Override
    public String getParameterValue(ResetRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }


    @Override
    public String getParameterValue(RefundRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentFormLogoRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentFormConfigurationRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(NotifyTransactionStatusRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(TransactionStatusRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(ContractParametersCheckRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getAccountInfo(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(CaptureRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(BuyerDetailsRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    /**
     * @param partnerConfiguration partner Configuration map
     * @param key                  property key
     * @return the corresponding String value
     */
    String safeGetValue(PartnerConfiguration partnerConfiguration, String key) {
        if (partnerConfiguration == null || key == null || key.isEmpty()) {
            return null;
        }
        return partnerConfiguration.getProperty(key);
    }

    /**
     * @param contractConfiguration contract Configuration map
     * @param key                   property key
     * @return the corresponding String value
     */
    String safeGetValue(ContractConfiguration contractConfiguration, String key) {
        if (contractConfiguration == null || key == null || contractConfiguration.getProperty(key) == null) {
            return null;
        }
        return contractConfiguration.getProperty(key).getValue();
    }

    /**
     * @param accountInfo a account info map
     * @param key         property key
     * @return the corresponding String value
     */
    String safeGetValue(Map<String, String> accountInfo, String key) {
        if (accountInfo == null || key == null) {
            return null;
        }
        return accountInfo.get(key);
    }

}
