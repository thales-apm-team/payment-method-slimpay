package com.payline.payment.slimpay.utils.properties.constants;

public class PaymentExecutionStatus {

    public static final String  PROCESSING = "processing";
    public static final String  REJECTED = "rejected";
    public static final String  PROCESSED = "processed";
    public static final String  NOT_PROCESSED = "notprocessed";
    public static final String  TRANSFORMED = "transformed";
    public static final String  CONTESTED = "contested";
    public static final String  TO_REPLAY = "toreplay";
    public static final String  TO_GENERATE = "togenerate";
    public static final String  TOP_PROCESS = "toprocess";

    private PaymentExecutionStatus() {
        //ras
    }
}
