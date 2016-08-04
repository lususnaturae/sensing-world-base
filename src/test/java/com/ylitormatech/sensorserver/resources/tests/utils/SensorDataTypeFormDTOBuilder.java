package com.ylitormatech.sensorserver.resources.tests.utils;

import com.ylitormatech.sensorserver.web.SensorDataTypeForm;

/**
 * Created by Perttu Vanharanta on 4.8.2016.
 */
public class SensorDataTypeFormDTOBuilder {

    private SensorDataTypeForm dto;

    public SensorDataTypeFormDTOBuilder(){
        dto = new SensorDataTypeForm();
    }

    public SensorDataTypeFormDTOBuilder name(String name){
        dto.setName(name);
        return this;
    }

    public SensorDataTypeForm build(){
        return dto;
    }
}
