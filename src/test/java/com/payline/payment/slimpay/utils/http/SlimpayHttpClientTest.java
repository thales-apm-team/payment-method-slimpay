package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.exception.SlimpayHttpException;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.exception.HttpServerErrorException;
import com.slimpay.hapiclient.exception.RelNotFoundException;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlimpayHttpClientTest {

    @InjectMocks
    private SlimpayHttpClient slimpayHttpClient;

    @Mock
    private HapiClient hapiClient;

    @BeforeEach
    public void setup() {
        slimpayHttpClient = SlimpayHttpClient.getInstance();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void doSendRequest_nominal() throws PluginTechnicalException, HttpException {
        // In the case the first call returns a Resource
        Resource expectedResource = Resource.fromJson( mockResourceCreateOrder() );
        doReturn( expectedResource )
                .when( hapiClient )
                .send( any(Follow.class) );

        // when calling doSendRequest
        Resource result = slimpayHttpClient.doSendRequest( hapiClient, mock(Follow.class) );

        // this resource is returned
        assertEquals( expectedResource, result );
    }

    @Test
    public void doSendRequest_retry() throws PluginTechnicalException, HttpException {
        // The HapiClient can't be reach or returns an error the first 2 times, then finally returns a Resource
        Resource expectedResource = Resource.fromJson( mockResourceCreateOrder() );
        when( hapiClient.send( any(Follow.class) ) )
                .thenThrow( HttpServerErrorException.class )
                .thenThrow( HttpServerErrorException.class )
                .thenReturn( expectedResource );

        // when calling doSendRequest
        Resource result = slimpayHttpClient.doSendRequest( hapiClient, mock(Follow.class) );

        // the resource is returned
        assertEquals( expectedResource, result );
    }

    @Test
    public void doSendRequest_httpException() throws HttpException {
        // An error is systematically encountered
        doThrow( HttpException.class )
                .when( hapiClient )
                .send( any(Follow.class) );

        // when calling doSendRequest, a SlimpayHttpException is thrown
        assertThrows( SlimpayHttpException.class, () ->
                slimpayHttpClient.doSendRequest(hapiClient, mock(Follow.class))
        );
    }

    @Test
    public void doSendRequest_relNotFoundException() throws HttpException {
        // A RelNotFoundException is thrown
        doThrow( RelNotFoundException.class )
                .when( hapiClient )
                .send( any(Follow.class) );

        // when calling doSendRequest, a PluginTechnicalException is thrown
        assertThrows( PluginTechnicalException.class, () ->
                slimpayHttpClient.doSendRequest(hapiClient, mock(Follow.class))
        );
    }


    /**
     * Exemple of a properly formatted JSON response body.
     * In this case, it is the state of an ordre after its creation.
     *
     * @return A JSON body as a String
     */
    private String mockResourceCreateOrder(){
        return "{ \"_links\" : { \"self\" : { \"href\" : \"https://api.preprod.slimpay.com/orders/a0abb1c5-5f5b-11e9-88ca-000000000000\" }, \"profile\" : { \"href\" : \"https://api.preprod.slimpay.com/alps/v1/orders\" }, \"https://api.slimpay.net/alps#get-creditor\" : { \"href\" : \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1\" }, \"https://api.slimpay.net/alps#get-subscriber\" : { \"href\" : \"https://api.preprod.slimpay.com/orders/a0abb1c5-5f5b-11e9-88ca-000000000000/subscriber\" }, \"https://api.slimpay.net/alps#get-order-items\" : { \"href\" : \"https://api.preprod.slimpay.com/orders/a0abb1c5-5f5b-11e9-88ca-000000000000/order-items\" }, \"https://api.slimpay.net/alps#user-approval\" : { \"href\" : \"https://checkout.preprod.slimpay.com/userApproval?accessCode=sprpu4ueOr1YCMlMymwoJsUnh5ItcPz7N1Jb0rtwZivfeS1mwogueObuTD9wxb\" }, \"https://api.slimpay.net/alps#extended-user-approval\" : { \"href\" : \"https://api.preprod.slimpay.com/creditors/paylinemerchanttest1/orders/HDEV-1555318206049/extended-user-approval{?mode}\", \"templated\" : true }, \"https://api.slimpay.net/alps#cancel-order\" : { \"href\" : \"https://api.preprod.slimpay.com/orders/a0abb1c5-5f5b-11e9-88ca-000000000000/cancellation\" } }, \"id\" : \"a0abb1c5-5f5b-11e9-88ca-000000000000\", \"reference\" : \"HDEV-1555318206049\", \"state\" : \"open.running\", \"locale\" : \"fr\", \"started\" : true, \"failureUrl\" : \"https://succesurl.com/\", \"successUrl\" : \"https://succesurl.com/\", \"cancelUrl\" : \"http://localhost/cancelurl.com/\", \"dateCreated\" : \"2019-04-15T08:51:15.366+0000\", \"dateStarted\" : \"2019-04-15T08:51:15.366+0000\", \"paymentScheme\" : \"SEPA.DIRECT_DEBIT.CORE\", \"sendUserApproval\" : true, \"checkoutActor\" : \"end_user\" }";
    }



}
