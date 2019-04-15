package com.payline.payment.slimpay.utils;

import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.pmapi.bean.common.FailureCause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class SlimpayErrorMapperTest {

    @Test
    void handleSlimpayError_null() {
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, SlimpayErrorMapper.handleSlimpayError((SlimpayError) null));
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, SlimpayErrorMapper.handleSlimpayError((SlimpayFailureResponse) null));

    }

}