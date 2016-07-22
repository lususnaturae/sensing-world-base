package com.ylitormatech.sensorserver.scheduled;

import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.service.JmsService;
import com.ylitormatech.sensorserver.domain.service.OnLogRemoveService;
import com.ylitormatech.sensorserver.domain.service.SensorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
@Component
public class OnLogProcess {


    Logger logger = Logger.getLogger(this.getClass().getName());


    @Autowired
    JmsService jmsService;

    @Autowired
    OnLogRemoveService onLogRemoveService;

    @Autowired
    SensorService sensorService;

    @Scheduled(cron = "0 */3 * * * ?")
    public void sendOnLogSensor(){
        List<SensorEntity> lists = sensorService.onLogNotSendList();
        System.out.println("Scheluded - sendOnLogSensor");
        if(!lists.isEmpty()){
            logger.info("Scheluded - List not empty");
            System.out.println("Scheluded - sendOnLogSensor List not empty");

            for (SensorEntity entity:lists) {
                System.out.println("In For-loop");
                jmsService.onLogProcessSensor(entity);
            }
        }

    }

    @Scheduled(cron = "0 */3 * * * ?")
    public void sendOnLogRemoves() {
        List<OnLogRemoveEntity> lists = onLogRemoveService.findRemoveList();

        System.out.println("Scheluded - sendOnLogRemoves");
        if(!lists.isEmpty()){
            logger.info("Scheluded - List not empty");
            System.out.println("Scheluded - List not empty");

            for (OnLogRemoveEntity entity:lists) {
                System.out.println("In For-loop");
                jmsService.onLogProcessRemove(entity);
            }
        }
    }

}
