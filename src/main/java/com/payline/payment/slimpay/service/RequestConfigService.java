package com.payline.payment.slimpay.service;

import com.payline.pmapi.bean.buyer.request.BuyerDetailsRequest;
import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

public interface RequestConfigService {

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request ResetRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(ResetRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request RefundRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(RefundRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request PaymentFormLogoRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(PaymentFormLogoRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request PaymentFormConfigurationRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(PaymentFormConfigurationRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request NotifyTransactionStatusRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(NotifyTransactionStatusRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request PaymentRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(PaymentRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request RedirectionPaymentRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(RedirectionPaymentRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request TransactionStatusRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(TransactionStatusRequest request, String key);

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request ContractParametersCheckRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(ContractParametersCheckRequest request, String key);


    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request CaptureRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(CaptureRequest request, String key);


    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request BuyerDetailsRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(BuyerDetailsRequest request, String key);

    default String safeGetValue(PartnerConfiguration partnerConfiguration, String key) {
        if (partnerConfiguration == null || key == null || key.isEmpty()) {
            return null;
        }
        return partnerConfiguration.getProperty(key);
    }


    default String safeGetValue(ContractConfiguration contractConfiguration, String key) {

        if (contractConfiguration == null || key == null || contractConfiguration.getProperty(key) == null) {
            return null;
        }
        return contractConfiguration.getProperty(key).getValue();
    }


}
