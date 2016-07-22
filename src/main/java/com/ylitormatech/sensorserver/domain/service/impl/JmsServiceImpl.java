package com.ylitormatech.sensorserver.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.service.JmsService;
import com.ylitormatech.sensorserver.domain.service.MessageService;
import com.ylitormatech.sensorserver.domain.service.OnLogRemoveService;
import com.ylitormatech.sensorserver.domain.service.SensorService;
import com.ylitormatech.sensorserver.utils.JmsSensor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
@Service
public class JmsServiceImpl implements JmsService {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    OnLogRemoveService onLogRemoveService;

    @Autowired
    SensorService sensorService;

    @Autowired
    MessageService messageService;

    @Override
    public ResponseEntity<String> newSensor(SensorEntity sensorEntity) {
        String reply = "";
        String json = "";
        ObjectMapper objectMapper = new ObjectMapper();
        JmsSensor jmsSensor = new JmsSensor();

        jmsSensor.setApikey(sensorEntity.getApikey());
        jmsSensor.setId(sensorEntity.getId());
        jmsSensor.setDatatype(sensorEntity.getSensorDatatypeEntities());

        try {
            json = objectMapper.writeValueAsString(jmsSensor);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"error\":\"sensor.create.error.json.parsing\"}");
        }

        logger.info(json);

        try {
            Message<String> message = MessageBuilder.withPayload(json)
                    .setHeader("Action", "New")
                    .build();
            logger.info(message);
            reply = messageService.sendMessage(message);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(("{\"error\":\"sensor.create.error.onlog.unavailable\"}"));
        }
        logger.info("Replyheader: " + reply);

        return null;
    }

    public void onLogProcessSensor(SensorEntity sensorEntity) {
        String reply = "";
        String json = "";
        boolean jsonSuccess = true;
        ObjectMapper objectMapper = new ObjectMapper();
        JmsSensor jmsSensor = new JmsSensor();

        jmsSensor.setApikey(sensorEntity.getApikey());
        jmsSensor.setId(sensorEntity.getId());
        jmsSensor.setDatatype(sensorEntity.getSensorDatatypeEntities());

        try {
            json = objectMapper.writeValueAsString(jmsSensor);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonSuccess=false;
        }

        if(jsonSuccess) {
            logger.info(json);
            try {
                Message<String> message = MessageBuilder.withPayload(json)
                        .setHeader("Action", "New")
                        .build();
                logger.info(message);
                reply = messageService.sendMessage(message);
                logger.info("Replyheader: " + reply);
                sensorService.restUpdateOnlog(sensorEntity);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }



    public void removeSensor(Integer id) {
        String reply = "";
        try {
            Message<Integer> message = MessageBuilder.withPayload(id)
                    .setHeader("Action", "Remove")
                    .build();

            reply = messageService.sendMessageInt(message);
        } catch (Exception e){
            logger.error(e.getMessage());
            onLogRemoveService.addRemove(id);
        }
        logger.info("Replyheader: " + reply);
    }

    public void onLogProcessRemove(OnLogRemoveEntity onLogRemoveEntity){
        String reply = "";
        try {
            Message<Integer> message = MessageBuilder.withPayload(onLogRemoveEntity.getSensorId())
                    .setHeader("Action", "Remove")
                    .build();

            reply = messageService.sendMessageInt(message);
            onLogRemoveService.deleteRemove(onLogRemoveEntity);
        } catch (Exception e){
            logger.error(e.getMessage());

        }

        logger.info("Replyheader: " + reply);
    }


}
