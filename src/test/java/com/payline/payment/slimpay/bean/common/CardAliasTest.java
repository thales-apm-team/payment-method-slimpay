package com.payline.payment.slimpay.bean.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardAliasTest {

    private CardAlias cardAlias;

    @Test
    public void testCardAlias() {
        String json = "{id:\"001-002\" ,reference:\"refCA\",status:\"my_status\",cardExpirationDate:\"some_date\"}";
        cardAlias = CardAlias.fromJson(json);

        Assertions.assertNotNull(cardAlias);
        Assertions.assertEquals("refCA", cardAlias.getReference());
        Assertions.assertEquals("001-002", cardAlias.getId());
        Assertions.assertEquals("my_status", cardAlias.getStatus());
        Assertions.assertEquals("some_date", cardAlias.getCardExpirationDate());
    }



}
