package com.ylitormatech.sensorserver.resources.tests.utils;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;

/**
 * Created by Perttu Vanharanta on 2.8.2016.
 */
public class SensorDataTypeDTOBuilder {
    private SensorDatatypeEntity dto;

    public SensorDataTypeDTOBuilder() {
        dto = new SensorDatatypeEntity();
    }

    public SensorDataTypeDTOBuilder id(Integer id){
        dto.setId(id);
        return this;
    }

    public SensorDataTypeDTOBuilder name(String name){
        dto.setName(name);
        return this;
    }

    public SensorDataTypeDTOBuilder labeltoken(String labeltoken){
        dto.setLabeltoken(labeltoken);
        return this;
    }

    public SensorDatatypeEntity build(){
        return dto;
    }
}
