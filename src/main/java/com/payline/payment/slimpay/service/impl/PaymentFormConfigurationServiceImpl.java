package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.service.ThalesPaymentFormConfigurationService;
import com.payline.payment.slimpay.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Thales on 27/08/2018.
 */
public class PaymentFormConfigurationServiceImpl implements ThalesPaymentFormConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentFormConfigurationServiceImpl.class);

    private I18nService i18n;

    public PaymentFormConfigurationServiceImpl() {
        i18n = I18nService.getInstance();
    }


    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest request) {
        NoFieldForm noFieldForm = NoFieldForm.NoFieldFormBuilder
                .aNoFieldForm()
                .withDisplayButton(true)
                .withButtonText(this.i18n.getMessage("payment.form.config.button.text", request.getLocale()))
                .withDescription(this.i18n.getMessage("payment.form.config.description", request.getLocale()))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(noFieldForm)
                .build();
    }

}