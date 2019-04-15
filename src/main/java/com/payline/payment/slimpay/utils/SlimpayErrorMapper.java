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
            case 631:
            case 637:
            case 642:
            case 643:
            case 644:
            case 645:
            case 646:
            case 647:
            case 648:
            case 652:
            case 919:
            case 901:
            case 908:
            case 910:
            case 911:
            case 916:
            case 917:
            case 921:
            case 1001:
            case 1004:
                return FailureCause.INVALID_DATA;

            case 904:
            case 907:
                return FailureCause.COMMUNICATION_ERROR;

            case 158:
            case 160:
            case 301:
            case 400:
            case 401:
            case 402:
            case 632:
            case 633:
            case 634:
            case 635:
            case 636:
            case 638:
            case 639:
            case 640:
            case 649:
            case 653:
            case 654:
            case 655:
            case 661:
            case 662:
            case 663:
            case 664:
            case 665:
            case 666:
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
            case 1002:
            case 1003:
            case 1005:
            case 1008:
            case 2000:
            case 2001:
                return FailureCause.REFUSED;

            case 408:
            case 409:
            case 412:
            case 651:
            case 656:
                return FailureCause.FRAUD_DETECTED;

            case 1009:
                return FailureCause.CANCEL;

            case 120:
                return FailureCause.SESSION_EXPIRED;

            case 100:
            case 103:
            case 199:
            case 905:
            case 906:
            case 924:
                return FailureCause.PAYMENT_PARTNER_ERROR;

            default:
                return FailureCause.PARTNER_UNKNOWN_ERROR;
        }
    }

    /**
     * Map Slimpay Payment issue with Payline FailureCause
     * @param error the slimpay returnRasonCode
     * @return
     */
    public static FailureCause handleSlimpayPaymentError(String error) {
        if( error == null ){
            return FailureCause.PAYMENT_PARTNER_ERROR;
        }

        String errorUpperCase = error.toUpperCase().replace("\"", "");
        switch (errorUpperCase) {
            case "CNOR":
            case "DNOR":
            case "SL01":
                return FailureCause.PAYMENT_PARTNER_ERROR;
            case "AC04":
            case "AC06":
            case "AC13":
            case "AG01":
            case "AM04":
            case "AM05":
            case "MD06":
            case "MD07":
            case "MS02":
            case "MS03":
            case "RR01":
            case "RR02":
            case "RR03":
            case "RR04":
                return FailureCause.REFUSED;
            case "AC01":
            case "BE05":
            case "MD01":
            case "MD02":
            case "RC01":
                return FailureCause.INVALID_DATA;
            case "AG02":
            case "FF01":
                return FailureCause.INVALID_FIELD_FORMAT;
            case "FOCR":
                return FailureCause.CANCEL;
            default:
                return FailureCause.PARTNER_UNKNOWN_ERROR;
        }
    }

}
