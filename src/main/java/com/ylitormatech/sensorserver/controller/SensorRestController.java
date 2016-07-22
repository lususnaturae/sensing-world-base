package com.ylitormatech.sensorserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.service.JmsService;
import com.ylitormatech.sensorserver.domain.service.SensorDatatypeService;
import com.ylitormatech.sensorserver.domain.service.SensorService;
import com.ylitormatech.sensorserver.utils.headerAgentInterceptor;
import com.ylitormatech.sensorserver.web.SensorForm;
import com.ylitormatech.sensorserver.web.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value(value = "${app.auth.url}")
    public String uri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private headerAgentInterceptor headerAgentInterceptor;

    @Autowired
    SensorService sensorService;

    @Autowired
    SensorDatatypeService sensorDatatypeService;


    @Autowired
    JmsService jmsService;


    @RequestMapping(value = "/create", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<String> createSensor(@RequestHeader("Authorization") String header, @RequestBody @Valid SensorForm sensorForm, BindingResult bindingResult){
        Integer userId;
        ResponseEntity<String> jmsResponse;

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.create.error.missing.value\"}");
        }

        userId = getUserInfo(header);
        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }
        //SensorEntity sensorEntity = sensorService.restAdd(sensorForm.getName(), sensorForm.getSensordatatypes(), userId);

        if(sensorService.restFindIsSensorNameExist(sensorForm.getName(),userId)) {
            if(sensorDatatypeService.isDatatypesValid(sensorForm.getSensordatatypes())) {
                SensorEntity sensorEntity = sensorService.restAdd(sensorForm.getName(), sensorForm.getSensordatatypes(), userId);
                jmsResponse = jmsService.newSensor(sensorEntity);
                if (jmsResponse == null) {
                    sensorService.restUpdateOnlog(sensorEntity);
                    return ResponseEntity.ok("sensor create");
                } else {
                    return jmsResponse;
                }
            }
            return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.create.error.invalid.datatype\"}");
        }
        return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("{\"error\":\"sensor.create.error.sensor.name.exist\"}");

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<String> showSensor(@RequestHeader("Authorization") String header,@PathVariable("id") Integer id) {
        logger.debug("Show SensorEntity Controller - GET");
        String json;
        Integer userId = getUserInfo(header);
        ObjectMapper objectMapper = new ObjectMapper();

        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"sensor.get.error.invalid.user\"}");
        }

        SensorEntity sensor = sensorService.restFind(id,userId);

        if (sensor==null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        try {
            json = objectMapper.writeValueAsString(sensor);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"error\":\"sensor.get.error.json.parsing\"}");
        }
        return ResponseEntity.ok(json);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<String> deleteSensor(@RequestHeader("Authorization") String header,@PathVariable("id") Integer id) {
        logger.debug("Delete SensorEntity Controller - DELETE");
        Integer userId = getUserInfo(header);

        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"sensor.delete.error.invalid.user\"}");
        }

        boolean exist = sensorService.restFindExist(id,userId);

        if (exist!=true){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        /*jms remove*/
        if(sensorService.restIsSentToOnLog(id,userId)) {
            jmsService.removeSensor(id);
        }
        sensorService.removeMySensor(id,userId);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSensor(@RequestHeader("Authorization") String header,@PathVariable("id") Integer id, @RequestBody @Valid SensorForm sensorForm, BindingResult bindingResult) {
        logger.debug("Update SensorEntity Controller - POST");
        Integer userId;

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.update.error.missing.value\"}");
        }

        userId = getUserInfo(header);
        if (userId == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"sensor.update.error.invalid.user\"}");
        }

        boolean exist = sensorService.restFindExist(id,userId);

        if (exist!=true){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        if(sensorService.restFindIsSensorNameEquals(sensorForm.getName(), id, userId)){
            if(sensorDatatypeService.isDatatypesValid(sensorForm.getSensordatatypes())) {
                sensorService.update(sensorForm.getName(),sensorForm.getSensordatatypes(),id,userId);
                return ResponseEntity.ok(null);
            }
            return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.update.error.invalid.datatype\"}");
        }
        if(sensorService.restFindIsSensorNameExist(sensorForm.getName(),userId)) {
            if(sensorDatatypeService.isDatatypesValid(sensorForm.getSensordatatypes())) {
                sensorService.update(sensorForm.getName(),sensorForm.getSensordatatypes(),id,userId);
                return ResponseEntity.ok(null);
                }

            return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.update.error.invalid.datatype\"}");
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("{\"error\":\"sensor.update.error.sensor.name.exist\"}");
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listSensors(@RequestHeader("Authorization") String header) {
        logger.debug("REST List SensorEntity Controller - GET");
        String json;
        Integer userid = getUserInfo(header);
        ObjectMapper objectMapper = new ObjectMapper();

        if (userid == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"sensor.list.error.invalid.user\"}");
        }

        List<SensorEntity> sensors = sensorService.restFindAll(userid);
        if(sensors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        try {
            json = objectMapper.writeValueAsString(sensors);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("{\"error\":\"sensor.list.error.json.parsing\"}");
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

    @RequestMapping(value = "/datatypelist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listSensorDatatypes(@RequestHeader("Authorization") String header) {
        logger.debug("REST List SensorDatatypeEntity Controller - GET");
        String json;
        Integer userid = getUserInfo(header);
        ObjectMapper objectMapper = new ObjectMapper();

        if (userid == -1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        List<SensorDatatypeEntity> sensorDatatypes = sensorDatatypeService.findAll();
        if(sensorDatatypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        try {
            json = objectMapper.writeValueAsString(sensorDatatypes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("error.json.parsing");
        }
        return ResponseEntity.ok(json);
    }



}