package com.payline.payment.slimpay.bean.common;

import com.payline.payment.slimpay.utils.Required;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Item Slimpay
//https://dev.slimpay.com/hapi/reference/order-items
public class SlimPayOrderItem extends SlimpayBean {

    private static final transient Logger LOGGER = LogManager.getLogger(SlimPayOrderItem.class);

    private String action;
    private String id;
    private CardAlias cardAlias;
    @Required
    private String type;
    @Required
    private Mandate mandate;
    private Payment payin;

    public String getAction() {
        return action;
    }

    public String getId() {
        return id;
    }

    public CardAlias getCardAlias() {
        return cardAlias;
    }

    public String getType() {
        return type;
    }


    public Mandate getMandate() {
        return mandate;
    }

    public Payment getPayin() {
        return payin;
    }

    private SlimPayOrderItem() {
        //ras
    }

    public SlimPayOrderItem(SlimPayOrderItem.Builder builder) {
        this.action = builder.action;
        this.id = builder.id;
        this.cardAlias = builder.cardAlias;
        this.type = builder.type;
        this.mandate = builder.mandate;
        this.payin = builder.payin;
    }

    public static class Builder {
        private String action;
        private String id;
        private CardAlias cardAlias;
        private String type;
        private Mandate mandate;
        private Payment payin;

        public static SlimPayOrderItem.Builder aSlimPayOrderItemBuilder() {
            return new SlimPayOrderItem.Builder();
        }

        public SlimPayOrderItem.Builder withAction(String action) {
            this.action = action;
            return this;
        }

        public SlimPayOrderItem.Builder withType(String type) {
            this.type = type;
            return this;
        }

        public SlimPayOrderItem.Builder withMandate(Mandate mandate) {
            this.mandate = mandate;
            return this;
        }

        public SlimPayOrderItem.Builder withPayin(Payment payin) {
            this.payin = payin;
            return this;
        }

        public SlimPayOrderItem.Builder withCardAlias(CardAlias alias) {
            this.cardAlias = alias;
            return this;
        }

        public SlimPayOrderItem.Builder verifyIntegrity() {

            if (this.type == null) {
                LOGGER.warn("SlimPayOrderItem must have a type when built");
            }
            if (this.mandate == null && this.payin == null) {
                LOGGER.warn("SlimPayOrderItem must have a mandate or a payin when built");
            }
            return this;
        }

        public SlimPayOrderItem build() {
            return new SlimPayOrderItem(this.verifyIntegrity());
        }

    }
}
