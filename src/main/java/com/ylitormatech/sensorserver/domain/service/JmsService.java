package com.ylitormatech.sensorserver.domain.service;

import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import org.springframework.http.ResponseEntity;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
public interface JmsService {

    public ResponseEntity<String> newSensor(SensorEntity sensorEntity);
    public void removeSensor(Integer id);
    public void onLogProcessRemove(OnLogRemoveEntity onLogRemoveEntity);
    public void onLogProcessSensor(SensorEntity sensorEntity);
}
