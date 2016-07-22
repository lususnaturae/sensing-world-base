package com.ylitormatech.sensorserver.domain.repository;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;

import java.util.List;

/**
 * Created by marco on 17.7.2016.
 */
public interface SensorDatatypeRepository {
    public List<SensorDatatypeEntity> findAll();
    public SensorDatatypeEntity findByName(String name);
    public boolean isDatatypeValid(String name);
}
