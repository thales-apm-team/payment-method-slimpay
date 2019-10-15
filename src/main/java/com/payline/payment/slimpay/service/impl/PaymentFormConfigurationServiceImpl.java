package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.service.DefaultPaymentFormConfigurationService;
import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;

import java.util.Locale;

/**
 * Created on 27/08/2018.
 */
public class PaymentFormConfigurationServiceImpl extends DefaultPaymentFormConfigurationService {

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest request) {
        Locale locale = request.getLocale();
        NoFieldForm noFieldForm = NoFieldForm.NoFieldFormBuilder
                .aNoFieldForm()
                .withDisplayButton(true)
                .withButtonText(this.i18n.getMessage(ConfigurationConstants.PAYMENT_BUTTON_TEXT, locale))
                .withDescription(this.i18n.getMessage(ConfigurationConstants.PAYMENT_BUTTON_DESC, locale))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(noFieldForm)
                .build();
    }

}