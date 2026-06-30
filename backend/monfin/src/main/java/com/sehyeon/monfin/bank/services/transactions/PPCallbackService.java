package com.sehyeon.monfin.bank.services.transactions;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.sehyeon.monfin.bank.dto.requests.ProcessorOTPCallbackRequest;

@Service
public class PPCallbackService {

    private final RestClient restClient;

    public PPCallbackService() {
        this.restClient = RestClient.create();
    }

    // makes a post request to the payment processor's url
    public void notifyPaymentProcessor(String callbackUrl, ProcessorOTPCallbackRequest request) {
        restClient.post()
            .uri(callbackUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve().toBodilessEntity();
    }
    
}
