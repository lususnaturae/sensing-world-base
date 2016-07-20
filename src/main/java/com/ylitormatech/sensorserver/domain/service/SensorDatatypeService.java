package com.ylitormatech.sensorserver.domain.service;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.web.SensorDataTypeForm;

import java.util.List;

/**
 * Created by marco on 17.7.2016.
 */
public interface SensorDatatypeService {

    public List<SensorDatatypeEntity> findAll();

    public boolean isDatatypesValid( List<SensorDataTypeForm> sensortypes);
}


