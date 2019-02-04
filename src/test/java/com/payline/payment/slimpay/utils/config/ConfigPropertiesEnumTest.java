package com.payline.payment.slimpay.utils.config;

import com.payline.payment.slimpay.utils.properties.service.ConfigPropertiesEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigPropertiesEnumTest {

    private String key;


    @Test
    public void getFromKeyKO() {
        key = ConfigPropertiesEnum.INSTANCE.get("BadKey");
        Assertions.assertNull(key);

    }

    @Test
    public void getFromKeyOK() {
        key = ConfigPropertiesEnum.INSTANCE.get("http.connectTimeout");
        Assertions.assertNotNull(key);
    }

}
