package com.ylitormatech.sensorserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import com.ylitormatech.sensorserver.utils.headerAgentInterceptor;

import java.util.*;

/**
 * Created by Perttu Vanharanta on 11.7.2016.
 */
@Controller
public class homeController {

    //private static final String USAGE_CHOICES = "usageChoices";

    private static final List<String> USAGE_CHOICES = Arrays.asList(
                "usagetoken.temperature", "usagetoken.location", "usagetoken.speed",
                "usagetoken.direction", "usagetoken.alert", "usagetoken.flag", "usagetoken.multifunction");


    @RequestMapping("/options")
    public ResponseEntity<String> home()
    {
        String json;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(USAGE_CHOICES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("error.json.parsing");
        }
        return ResponseEntity.ok(json);
    }
}
