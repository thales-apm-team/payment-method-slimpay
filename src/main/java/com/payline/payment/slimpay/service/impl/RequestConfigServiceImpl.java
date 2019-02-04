package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.service.RequestConfigService;
import com.payline.pmapi.bean.buyer.request.BuyerDetailsRequest;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.util.HashMap;
import java.util.Map;

public enum RequestConfigServiceImpl implements RequestConfigService {

    INSTANCE;


    public enum PaylineParameterType {
        CONTRACT_CONFIGURATION_PARAMETER,
        PARTNER_CONFIGURATION_PAMAETER;
    }

    /**
     * TODO : complete with all contract and configuration parameters; use MDP constants as keys.
     */
    public static final Map<String, PaylineParameterType> PARAMETERS_MAP = new HashMap<String, PaylineParameterType>() {{
        put("name", PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        put("city", PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
        put("zip", PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER);
    }};

    RequestConfigServiceImpl() {
        // ras
    }

    @Override
    public String getParameterValue(ResetRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }


    @Override
    public String getParameterValue(RefundRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentFormLogoRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentFormConfigurationRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(NotifyTransactionStatusRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(PaymentRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(RedirectionPaymentRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(TransactionStatusRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(ContractParametersCheckRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(CaptureRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

    @Override
    public String getParameterValue(BuyerDetailsRequest request, String key) {
        PaylineParameterType paylineParameterType = PARAMETERS_MAP.get(key);
        if (PaylineParameterType.CONTRACT_CONFIGURATION_PARAMETER == paylineParameterType) {
            return safeGetValue(request.getContractConfiguration(), key);
        } else if (PaylineParameterType.PARTNER_CONFIGURATION_PAMAETER == paylineParameterType) {
            return safeGetValue(request.getPartnerConfiguration(), key);
        }
        return null;
    }

}
