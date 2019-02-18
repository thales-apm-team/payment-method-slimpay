package com.payline.payment.slimpay.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataCheckerTest {

    @Test
    void isISO3166() {
        Assertions.assertTrue(DataChecker.isISO3166("FR"));
        Assertions.assertFalse(DataChecker.isISO3166("fr"));
        Assertions.assertFalse(DataChecker.isISO3166("1A"));
        Assertions.assertFalse(DataChecker.isISO3166(""));
        Assertions.assertFalse(DataChecker.isISO3166(null));
    }

    @Test
    void isNumeric() {
        Assertions.assertTrue(DataChecker.isNumeric("0"));
        Assertions.assertTrue(DataChecker.isNumeric(""));
        Assertions.assertFalse(DataChecker.isNumeric(null));
        Assertions.assertFalse(DataChecker.isNumeric("a"));
        Assertions.assertFalse(DataChecker.isNumeric("10.0"));
    }

    @Test
    void isEmpty() {
        Assertions.assertTrue(DataChecker.isEmpty(null));
        Assertions.assertTrue(DataChecker.isEmpty(""));
        Assertions.assertFalse(DataChecker.isEmpty("foo"));
    }
}