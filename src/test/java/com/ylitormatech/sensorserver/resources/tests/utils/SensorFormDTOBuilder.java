package com.ylitormatech.sensorserver.resources.tests.utils;

import com.ylitormatech.sensorserver.web.SensorDataTypeForm;
import com.ylitormatech.sensorserver.web.SensorForm;

import java.util.List;

/**
 * Created by Perttu Vanharanta on 4.8.2016.
 */
public class SensorFormDTOBuilder {
    private SensorForm dto;

    public SensorFormDTOBuilder(){
        dto = new SensorForm();
    }

    public SensorFormDTOBuilder name(String name){
        dto.setName(name);
        return this;
    }

    public SensorFormDTOBuilder sensorFormDataTypesDTO(List<SensorDataTypeForm> dataTypeForms){
        dto.setSensordatatypes(dataTypeForms);
        return this;
    }


    public SensorForm build(){
        return dto;
    }
}
