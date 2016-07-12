package com.ylitormatech.sensorserver.utils;

import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by Perttu Vanharanta on 11.7.2016.
 */
public class headerAgentInterceptor implements ClientHttpRequestInterceptor{

    private String bearer;

    private String getBearer(){return this.bearer;}

    public void setBearer(String header){
        this.bearer = header;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();

        headers.add("Authorization", getBearer());
        return execution.execute(request,body);
    }
}
