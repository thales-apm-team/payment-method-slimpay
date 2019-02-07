package com.payline.payment.slimpay.bean;

import com.payline.payment.slimpay.utils.InvalidRequestException;
import com.payline.payment.slimpay.utils.SlimpayCardConstants;
import com.payline.pmapi.bean.payment.ContractConfiguration;

import java.util.Base64;

public abstract class SlimpayRequest {
    private transient String authenticationHeader;

    SlimpayRequest(ContractConfiguration configuration) throws InvalidRequestException {
        if (configuration == null || configuration.getProperty(SlimpayCardConstants.AUTHORISATIONKEY_KEY).getValue() == null) {
            throw new InvalidRequestException("SlimpayRequest must have an authorisation key when created");
        } else {
            this.authenticationHeader = "Basic " + encodeToBase64(configuration.getProperty(SlimpayCardConstants.AUTHORISATIONKEY_KEY).getValue());
        }
    }

    public String getAuthenticationHeader() {
        return authenticationHeader;
    }

    public static String encodeToBase64(String toEncode) {
        if (toEncode == null) toEncode = "";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}
