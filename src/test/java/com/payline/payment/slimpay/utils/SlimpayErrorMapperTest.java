package com.payline.payment.slimpay.utils;

import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.pmapi.bean.common.FailureCause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class SlimpayErrorMapperTest {

    private int[] invlidData = new int[]{101, 105, 108, 122, 123, 124, 125, 127, 128, 129, 133, 134, 135, 141, 142, 143, 144, 146, 179, 180, 188, 191, 196, 205, 230, 231, 232, 407, 637, 639, 642, 643, 644, 645, 646, 647, 648, 666, 919, 901, 908, 910, 911, 1004};
    private int[] communicationError = new int[]{402, 652, 904, 907, 916, 917, 921, 1008, 2000, 2001};
    private int[] refused = new int[]{158, 160, 301, 632, 633, 634, 635, 636, 653, 654, 655, 661, 662, 663, 664, 665, 667, 668, 902, 903, 912, 913, 914, 915, 918, 920, 922, 923, 925};
    private int[] fraudDetected = new int[]{651, 656};
    private int[] partnerUnknownError = new int[]{100};
    private int[] cancel = new int[]{1009};
    private int[] sessionExpired = new int[]{120};
    private int[] paymentPartnerError = new int[]{103, 199, 631, 638, 640, 649, 905, 906, 924};


    @Test
    void handleSlimpayError_Null() {

        Assertions.assertNotNull(SlimpayErrorMapper.handleSlimpayError((SlimpayError) null));
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, SlimpayErrorMapper.handleSlimpayError((SlimpayError) null));

        Assertions.assertNotNull(SlimpayErrorMapper.handleSlimpayError((SlimpayFailureResponse) null));
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, SlimpayErrorMapper.handleSlimpayError((SlimpayFailureResponse) null));

    }

    @Test
    void handleSlimpayError_fullTest() {
        Map<FailureCause, int[]> testMap = new HashMap<>();
        testMap.put(FailureCause.INVALID_DATA, invlidData);
        testMap.put(FailureCause.COMMUNICATION_ERROR, communicationError);
        testMap.put(FailureCause.REFUSED, refused);
        testMap.put(FailureCause.FRAUD_DETECTED, fraudDetected);
        testMap.put(FailureCause.PARTNER_UNKNOWN_ERROR, partnerUnknownError);
        testMap.put(FailureCause.CANCEL, cancel);
        testMap.put(FailureCause.SESSION_EXPIRED, sessionExpired);
        testMap.put(FailureCause.PAYMENT_PARTNER_ERROR, paymentPartnerError);


        for (Map.Entry<FailureCause, int[]> entry : testMap.entrySet()) {
            int[] errorValue = entry.getValue();

            for (int i : errorValue) {
                SlimpayError slimpayError = new SlimpayError(i, null, null);
                Assertions.assertEquals(entry.getKey(), SlimpayErrorMapper.handleSlimpayError(slimpayError));
            }
        }
    }
}