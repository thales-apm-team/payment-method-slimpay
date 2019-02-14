package com.payline.payment.slimpay.utils.properties.constants;

public class OrderStatus {

    public static final String  OPEN = "open";
    public static final String  OPEN_RUNNING = "open.running";
    public static final String  OPEN_NOT_RUNNING = "open.not_running";
    public static final String  CLOSED_ABORTED = "closed.aborted";
    public static final String  CLOSED_ABORTED_BY_CLIENT = "closed.aborted.aborted_byclient";
    public static final String  CLOSED_ABORTED_BY_SERVER = "closed.aborted.aborted_byserver";
    public static final String  CLOSED_COMPLETED = "closed.completed";


    private OrderStatus(){
        //ras
    }
}
