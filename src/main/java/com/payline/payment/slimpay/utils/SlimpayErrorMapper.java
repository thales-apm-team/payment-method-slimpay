package com.payline.payment.slimpay.utils;

import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.pmapi.bean.common.FailureCause;

public class SlimpayErrorMapper {

    public static FailureCause handleSlimpayError(SlimpayError error) {

        int spErrorCode = error.getCode();
        FailureCause paylineCause;

        switch (spErrorCode) {
            case 101:
            case 105:
            case 108:
            case 122:
            case 188:
            case 123:
            case 124:
            case 125:
            case 127:
            case 128:
            case 129:
            case 146:
            case 133:
            case 134:
            case 135:
            case 141:
            case 142:
            case 143:
            case 144:
            case 180:
            case 191:
            case 196:
            case 205:
            case 230:
            case 232:
            case 231:
            case 637:
            case 639:
            case 642:
            case 643:
            case 644:
            case 645:
            case 646:
            case 647:
            case 648:
            case 666:
            case 919:
            case 179:
            case 407:
            case 1004:
                paylineCause = FailureCause.INVALID_DATA;
                break;

            case 2001:
            case 2000:
            case 1008:
            case 402:
            case 921:
            case 917:
            case 916:
            case 907:
            case 904:
            case 652:
                paylineCause = FailureCause.COMMUNICATION_ERROR;
                break;

            case 103:
            case 199:
            case 631:
            case 640:
            case 638:
            case 649:
            case 906:

                paylineCause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;

            case 301:
            case 632:
            case 633:
            case 634:
            case 635:
            case 636:
            case 641:
            case 650:
                paylineCause = FailureCause.REFUSED;
                break;

            default:
                paylineCause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;
        }




        return paylineCause;


    }
}
