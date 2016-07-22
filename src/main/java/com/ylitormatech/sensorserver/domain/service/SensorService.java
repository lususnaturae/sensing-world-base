package com.ylitormatech.sensorserver.domain.service;

import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.web.SensorDataTypeForm;

import java.util.List;

/**
 * Created by Marco Ylitörmä on 02/05/16.
 */
public interface SensorService {

    public SensorEntity restAdd(String name, List<SensorDataTypeForm> sensorTypes, Integer userId);

    public boolean restFindExist(Integer id, Integer userId);
    public boolean restFindIsSensorNameExist(String name, Integer userId);
    public boolean restFindIsSensorNameEquals(String name, Integer id, Integer userId);
    public boolean restIsSentToOnLog(Integer id, Integer userId);

    public SensorEntity restFind(Integer id, Integer userId);
    public SensorEntity findMySensor(Integer id, Integer userid);

    public void update(String name, List<SensorDataTypeForm>sensorTypes, Integer id, Integer userId);
    public void restUpdateOnlog(SensorEntity sensorEntity);


    public void removeMySensor(Integer id, Integer userid);

    public List<SensorEntity> restFindAll(Integer userId);
    public List<SensorEntity> onLogNotSendList();

}
