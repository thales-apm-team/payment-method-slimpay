package com.payline.payment.slimpay.bean.common;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultMandate;
import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultPayin;


@PrepareForTest({SlimPayOrderItem.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlimPayOrderItemTest {

    SlimPayOrderItem orderItem;
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {

        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(SlimPayOrderItem.class, "LOGGER", mockLogger);
    }

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

    @Test
    public void SlimpayOrderItemMandateWithoutReference(){
        orderItem = SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("signMandate")
                .build();
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(orderItem.MANDATE_WARN));

    }
    @Test
    public void SlimpayOrderItemMandateWithoutType(){
        orderItem = SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .build();
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(orderItem.TYPE_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(orderItem.TYPE_WARN));

    }
}
