package com.payline.payment.slimpay.business;

import com.payline.payment.slimpay.exception.InvalidDataException;
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

public interface RequestConfigBusiness {

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request ResetRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(ResetRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request RefundRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(RefundRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request PaymentFormLogoRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(PaymentFormLogoRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request PaymentFormConfigurationRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(PaymentFormConfigurationRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request NotifyTransactionStatusRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(NotifyTransactionStatusRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request PaymentRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(PaymentRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request TransactionStatusRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(TransactionStatusRequest request, String key) throws InvalidDataException;

    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request ContractParametersCheckRequest
     * @param key     property key
     * @return teh coreesponding String value
     */
    String getParameterValue(ContractParametersCheckRequest request, String key) throws InvalidDataException;


    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request CaptureRequest
     * @param key     property key
     * @return the corresponding String value
     */
    String getParameterValue(CaptureRequest request, String key) throws InvalidDataException;


    /**
     * Use PARAMETERS_MAP to read a property in ContractConfiguration or in PartnerConfiguration.
     *
     * @param request BuyerDetailsRequest
     * @param key     property key
     * @return the corresponding String value
     */
    String getParameterValue(BuyerDetailsRequest request, String key) throws InvalidDataException;

}

