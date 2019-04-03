package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import java.util.Currency;

import static com.payline.payment.slimpay.utils.BeansUtils.*;
import static com.payline.payment.slimpay.utils.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlimpayHttpClientTest {


    @Spy
    SlimpayHttpClient mockedClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void getSlimpayResponseTestNull() throws Exception {
        Assertions.assertThrows(HttpCallException.class, () -> {
            SlimpayHttpClient httpClient = mock(SlimpayHttpClient.class);
            SlimpayResponse response = Whitebox.invokeMethod(httpClient,
                    "getSlimpayResponse", (Object) null);
        });

    }

    @Test
    public void getSlimpayResponseTestOK() throws Exception {
        SlimpayHttpClient httpClient = mock(SlimpayHttpClient.class);

        Resource resource = createMockedResourceOrder("test");
        SlimpayResponse response = Whitebox.invokeMethod(httpClient,
                "getSlimpayResponse", resource);
        Assertions.assertSame(SlimpayOrderResponse.class, response.getClass());
        SlimpayOrderResponse order = (SlimpayOrderResponse) response;
        Assertions.assertNotNull(order.getId());
        Assertions.assertNotNull(order.getReference());
        Assertions.assertNotNull(order.getState());

    }

}
