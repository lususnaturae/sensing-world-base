package com.ylitormatech.sensorserver.resources.tests.utils;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;

import java.util.Set;

/**
 * Created by Perttu Vanharanta on 2.8.2016.
 */
public class SensorDTOBuilder {

    private SensorEntity dto;

    public SensorDTOBuilder() {
        dto = new SensorEntity();
    }

    public SensorDTOBuilder id(Integer id) {
        dto.setId(id);
        return this;
    }

    public SensorDTOBuilder name(String name) {
        dto.setName(name);
        return this;
    }

    public SensorDTOBuilder lat(double lat) {
        dto.setLat(lat);
        return this;
    }

    public SensorDTOBuilder lon(double lon) {
        dto.setLon(lon);
        return this;
    }

    public SensorDTOBuilder sensorDataTypeDTOs(Set<SensorDatatypeEntity> dataTypeDTOs){
        dto.setSensorDatatypeEntities(dataTypeDTOs);
        return this;
    }

    public SensorDTOBuilder apikey(String apikey){
        dto.setApikey(apikey);
        return this;
    }

    public SensorDTOBuilder userid(Integer userid){
        dto.setUserid(userid);
        return this;
    }

    public SensorDTOBuilder active(Boolean active){
        dto.setActive(active);
        return this;
    }

    public SensorDTOBuilder onLogSend(Boolean onlogsend){
        dto.setOnLogSend(onlogsend);
        return this;
    }

    public SensorEntity build() {
        return dto;
    }
}
