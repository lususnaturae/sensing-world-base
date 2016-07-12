package com.ylitormatech.sensorserver.domain.repository;

import com.ylitormatech.sensorserver.domain.entity.SensorEntity;

import java.util.List;

/**
 * Created by Marco Ylitörmä on 02/05/16.
 */
public interface SensorRepository {

    public void add(SensorEntity sensorEntity);

    public boolean restFindIdUserIdExist(Integer id, Integer userId);
    public SensorEntity restFindIdUserId(Integer id, Integer userId);
    SensorEntity findMySensor(Integer id, Integer userid);

    void update(SensorEntity sensorEntity);

    void removeMySensor(Integer id, Integer userid);

    public List<SensorEntity> restFindAll(Integer userId);
}
