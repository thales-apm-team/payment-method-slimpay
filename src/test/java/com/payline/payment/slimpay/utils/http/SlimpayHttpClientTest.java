package com.payline.payment.slimpay.utils.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@PrepareForTest(SlimpayHttpClient.class)
public class SlimpayHttpClientTest {


    //ToDO  mock http call, not mocked now to check if they work
    @Spy
    SlimpayHttpClient testedClient;

    @InjectMocks
    SlimpayHttpClient client;

    @Mock
    CloseableHttpClient closableClient;


    private Map<String, String> params;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(client, "client", closableClient);

        params = new HashMap<>();
        //TODO
    }

    @Test
    public void doGet() throws IOException, URISyntaxException {

        // TODO

    }

    @Test
    public void doPost() throws Exception {

        // TODO

    }

    public void cancelPaymentOK(){


    }


}
