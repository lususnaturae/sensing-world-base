package com.ylitormatech.sensorserver.utils;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;

import java.util.List;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
public class JmsSensor {
    String apikey;
    Integer id;
    List<SensorDatatypeEntity> datatype;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<SensorDatatypeEntity> getDatatype() {
        return datatype;
    }

    public void setDatatype(List<SensorDatatypeEntity> datatype) {
        this.datatype = datatype;
    }
}
