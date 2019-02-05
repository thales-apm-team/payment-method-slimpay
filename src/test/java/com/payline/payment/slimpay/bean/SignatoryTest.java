package com.payline.payment.slimpay.bean;

import com.payline.payment.slimpay.bean.common.Signatory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultBillingAddress;

public class SignatoryTest {

    private Signatory signatory;

    @Test
    public void signatoryOk(){
        signatory = Signatory.Builder.aSignatoryBuilder()
                .withfamilyName("Doe")
                .withGivenName("John")
                .withHonorificPrefix("Mr")
                .withBilingAddress(createDefaultBillingAddress())
                .withEmail("toto@emailcom")
                .withTelephone("+33725262729")
                .build();

        String jsonSignatory = signatory.toString();
        Assertions.assertTrue(jsonSignatory.contains("Doe"));
        Assertions.assertTrue(jsonSignatory.contains("John"));
        Assertions.assertTrue(jsonSignatory.contains("toto@emailcom"));
        Assertions.assertTrue(jsonSignatory.contains("+33725262729"));
        Assertions.assertTrue(jsonSignatory.contains("Mr"));
        Assertions.assertTrue(jsonSignatory.contains("billingAddress"));
    }

    @Test
    public void signatoryKO(){
        signatory = Signatory.Builder.aSignatoryBuilder()
                .withHonorificPrefix("Mr")
                .withBilingAddress(createDefaultBillingAddress())
                .withEmail("toto@emailcom")
                .withTelephone("+33725262729")
                .build();
        //todo assertion log  ecrit 2 messages
    }
}
