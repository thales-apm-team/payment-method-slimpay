package com.payline.payment.slimpay.utils.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlimpayHttpClientTest {


    @Spy
    SlimpayHttpClient mockedClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }

}
