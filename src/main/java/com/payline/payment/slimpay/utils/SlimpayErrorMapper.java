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
            case 910:
            case 901:
            case 908:
            case 911:
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


            case 301:
            case 632:
            case 633:
            case 634:
            case 635:
            case 636:
            case 653:
            case 654:
            case 655:
            case 661:
            case 662:
            case 663:
            case 664:
            case 665:
            case 667:
            case 668:
            case 902:
            case 903:
            case 912:
            case 913:
            case 914:
            case 915:
            case 918:
            case 920:
            case 922:
            case 923:
            case 925:
            case 158:
            case 160:
                paylineCause = FailureCause.REFUSED;
                break;

            case 651:
            case 656:
                paylineCause = FailureCause.FRAUD_DETECTED;
                break;

            case 100:
                paylineCause = FailureCause.PARTNER_UNKNOWN_ERROR;
                break;

            case 1009:
                paylineCause = FailureCause.CANCEL;
                break;

            case 120:
                paylineCause = FailureCause.SESSION_EXPIRED;
                break;

            case 103:
            case 199:
            case 631:
            case 640:
            case 638:
            case 649:
            case 905:
            case 906:
            case 924:
            default:
                paylineCause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;
        }

        return paylineCause;


    }

    private SlimpayErrorMapper() {
        //ras
    }
}
