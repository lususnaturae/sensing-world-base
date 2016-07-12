package com.ylitormatech.sensorserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.service.SensorService;
import com.ylitormatech.sensorserver.utils.headerAgentInterceptor;
import com.ylitormatech.sensorserver.web.SensorForm;
import com.ylitormatech.sensorserver.web.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Perttu Vanharanta on 12/07/16.
 */
@RestController
@RequestMapping("/api/sensors")
public class SensorRestController {

    Logger logger = Logger.getLogger(this.getClass().getName());
    public final String uri = "http://localhost:8080/user/id";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private headerAgentInterceptor headerAgentInterceptor;

    @Autowired
    SensorService sensorService;

    @RequestMapping(value = "/create", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<String> createSensor(@RequestHeader("Authorization") String header, @RequestBody @Valid SensorForm sensorForm, BindingResult bindingResult){
        Integer userId;

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.create.error.missing.value\"}");
        }

        userId = getUserInfo(header);
        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }
        sensorService.restAdd(sensorForm.getName(), sensorForm.getUsagetoken(), userId);
        return ResponseEntity.ok("sensor create");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<String> showSensor(@RequestHeader("Authorization") String header,@PathVariable("id") Integer id) {
        logger.debug("Show SensorEntity Controller - GET");
        String json;
        Integer userId = getUserInfo(header);
        ObjectMapper objectMapper = new ObjectMapper();

        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        SensorEntity sensor = sensorService.restFind(id,userId);

        if (sensor==null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        try {
            json = objectMapper.writeValueAsString(sensor);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("error.json.parsing");
        }
        return ResponseEntity.ok(json);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<String> deleteSensor(@RequestHeader("Authorization") String header,@PathVariable("id") Integer id) {
        logger.debug("Delete SensorEntity Controller - DELETE");
        Integer userid = getUserInfo(header);

        if (userid == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        boolean exist = sensorService.restFindExist(id,userid);

        if (exist!=true){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        sensorService.removeMySensor(id,userid);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> updateSensor(@RequestHeader("Authorization") String header,@PathVariable("id") Integer id, @RequestBody @Valid SensorForm sensorForm, BindingResult bindingResult) {
        logger.debug("Update SensorEntity Controller - POST");
        Integer userId;

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.create.error.missing.value\"}");
        }

        userId = getUserInfo(header);
        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        boolean exist = sensorService.restFindExist(id,userId);

        if (exist!=true){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        sensorService.update(sensorForm.getName(),sensorForm.getUsagetoken(),id,userId);
        return ResponseEntity.ok(null);
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listSensors(@RequestHeader("Authorization") String header) {
        logger.debug("REST List SensorEntity Controller - GET");
        String json;
        Integer userid = getUserInfo(header);
        ObjectMapper objectMapper = new ObjectMapper();

        if (userid == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        List<SensorEntity> sensors = sensorService.restFindAll(userid);
        if(sensors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        try {
            json = objectMapper.writeValueAsString(sensors);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("error.json.parsing");
        }
        return ResponseEntity.ok(json);    }


    private Integer getUserInfo(String header){
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