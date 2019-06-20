package com.payline.payment.slimpay.service;

import com.payline.payment.slimpay.utils.i18n.I18nService;
import com.payline.payment.slimpay.utils.properties.service.LogoProperties;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentFormConfigurationService;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static com.payline.payment.slimpay.utils.properties.constants.LogoConstants.*;

public abstract class DefaultPaymentFormConfigurationService implements PaymentFormConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(DefaultPaymentFormConfigurationService.class);
    private static final I18nService i18n = I18nService.getInstance();

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {

        Locale locale = paymentFormLogoRequest.getLocale();

        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withHeight(Integer.valueOf(LogoProperties.INSTANCE.get(LOGO_HEIGHT)))
                .withWidth(Integer.valueOf(LogoProperties.INSTANCE.get(LOGO_WIDTH)))
                .withTitle(i18n.getMessage(LogoProperties.INSTANCE.get(LOGO_TITLE), locale))
                .withAlt(i18n.getMessage(LogoProperties.INSTANCE.get(LOGO_ALT), locale))
                .build();
    }

    @Override
    public PaymentFormLogo getLogo(String s, Locale locale) {

        String fileName = LogoProperties.INSTANCE.get(LOGO_FILE_NAME);
        InputStream input = DefaultPaymentFormConfigurationService.class.getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            LOGGER.error("Unable to load the logo {}", LOGO_FILE_NAME);
            throw new RuntimeException("Unable to load the logo " + LOGO_FILE_NAME);
        }
        try {
            // Read logo file
            BufferedImage logo = ImageIO.read(input);

            // Recover byte array from image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, LogoProperties.INSTANCE.get(LOGO_FORMAT), baos);

            return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                    .withFile(baos.toByteArray())
                    .withContentType(LogoProperties.INSTANCE.get(LOGO_CONTENT_TYPE))
                    .build();
        } catch (IOException e) {
            LOGGER.error("Unable to load the logo", e);
            throw new RuntimeException(e);
        }
    }

}
