package com.ylitormatech.sensorserver.domain.service.impl;

import com.ylitormatech.sensorserver.domain.service.RestService;
import com.ylitormatech.sensorserver.utils.headerAgentInterceptor;
import com.ylitormatech.sensorserver.web.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Perttu Vanharanta on 3.8.2016.
 */

@Service
public class RestServiceImpl implements RestService{

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Value(value = "${app.auth.url}")
    public String uri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private headerAgentInterceptor headerAgentInterceptor;



    public Integer getUserInfo(String header){
        UserInfo userInfo;

        headerAgentInterceptor.setBearer(header);

        try {
            userInfo = restTemplate.getForObject(uri, UserInfo.class);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return -1;
        }
        System.out.println(userInfo.getId());
        return userInfo.getId();
    }
}
