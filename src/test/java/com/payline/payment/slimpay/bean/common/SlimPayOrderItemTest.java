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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;


@PrepareForTest({SlimPayOrderItem.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlimPayOrderItemTest {

    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(SlimPayOrderItem.class, "LOGGER", mockLogger);
    }

    @Test
    public void SlimpayOrderItemOK(){
        SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("payment")
                .withPayin(createDefaultPayin("reference payment"))
                .build();

        Mockito.verify(mockLogger, never()).warn(anyString());
    }

    @Test
    public void SlimpayOrderItemMandateOK(){
        SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("signMandate")
                .withMandate(createDefaultMandate("reference mandate"))
                .build();

        Mockito.verify(mockLogger, never()).warn(anyString());
    }

    @Test
    public void SlimpayOrderItemMandateWithoutReference(){
        SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .withType("signMandate")
                .build();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimPayOrderItem.MANDATE_WARN));
    }
    @Test
    public void SlimpayOrderItemMandateWithoutType(){
        SlimPayOrderItem.Builder.aSlimPayOrderItemBuilder()
                .build();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(SlimPayOrderItem.TYPE_WARN));
    }
}
