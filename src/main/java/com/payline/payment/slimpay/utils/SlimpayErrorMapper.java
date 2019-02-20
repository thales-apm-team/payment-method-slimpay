package com.payline.payment.slimpay.utils;

import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.pmapi.bean.common.FailureCause;

public class SlimpayErrorMapper {


    private SlimpayErrorMapper() {
        //ras
    }

    public static FailureCause handleSlimpayError(SlimpayFailureResponse slimpayOrderFailureResponse) {

        if (slimpayOrderFailureResponse == null) {
            return FailureCause.PAYMENT_PARTNER_ERROR;
        }

        return handleSlimpayError(slimpayOrderFailureResponse.getError());
    }

    public static FailureCause handleSlimpayError(SlimpayError error) {

        if (error == null) {
            return FailureCause.PAYMENT_PARTNER_ERROR;
        }

        int spErrorCode = error.getCode();

        switch (spErrorCode) {
            case 101:
            case 105:
            case 108:
            case 122:
            case 123:
            case 124:
            case 125:
            case 127:
            case 128:
            case 129:
            case 133:
            case 134:
            case 135:
            case 141:
            case 142:
            case 143:
            case 144:
            case 146:
            case 179:
            case 180:
            case 188:
            case 191:
            case 196:
            case 205:
            case 230:
            case 231:
            case 232:
            case 407:
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
            case 901:
            case 908:
            case 910:
            case 911:
            case 1004:
                return FailureCause.INVALID_DATA;

            case 402:
            case 652:
            case 904:
            case 907:
            case 916:
            case 917:
            case 921:
            case 1008:
            case 2000:
            case 2001:
                return FailureCause.COMMUNICATION_ERROR;


            case 158:
            case 160:
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
                return FailureCause.REFUSED;

            case 651:
            case 656:
                return FailureCause.FRAUD_DETECTED;

            case 100:
                return FailureCause.PARTNER_UNKNOWN_ERROR;

            case 1009:
                return FailureCause.CANCEL;

            case 120:
                return FailureCause.SESSION_EXPIRED;

            case 103:
            case 199:
            case 631:
            case 638:
            case 640:
            case 649:
            case 905:
            case 906:
            case 924:
            default:
                return FailureCause.PAYMENT_PARTNER_ERROR;
        }


    }
}
