package com.ylitormatech.sensorserver.web;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;


/**
 * Created by Marco Ylitörmä on 03/05/16.
 */
public class SensorForm {

    @NotEmpty
    String name;
    @NotEmpty
    List<SensorDatatypeEntity> sensordatatypes;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SensorDatatypeEntity> getSensorDatatypes() {
        return this.sensordatatypes;
    }

    public void setUsagetoken(List<SensorDatatypeEntity> sensordatatypes) {
        this.sensordatatypes = sensordatatypes;
    }

}
