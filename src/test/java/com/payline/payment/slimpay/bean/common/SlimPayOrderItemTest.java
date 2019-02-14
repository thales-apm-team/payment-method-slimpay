package com.payline.payment.slimpay.bean.common;

import org.junit.jupiter.api.Test;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultMandate;
import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultPayin;

public class SlimPayOrderItemTest {

    SlimPayOrderItem orderItem;

    @Test
    public void SlimpayOrderItemOK(){
        orderItem = SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("payment")
                .withPayin(createDefaultPayin("reference payment"))
                .build();


    }

    @Test
    public void SlimpayOrderItemMandateOK(){
        orderItem = SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("signMandate")
                .withMandate(createDefaultMandate("reference mandate"))
                .build();

    }

}
