package com.ylitormatech.sensorserver.web;

import com.ylitormatech.sensorserver.web.SensorDataTypeForm;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;


/**
 * Created by Marco Ylitörmä on 03/05/16.
 */
public class SensorForm {

    @NotEmpty
    String name;
    @Valid @NotEmpty
    List<SensorDataTypeForm> sensordatatypes;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<SensorDataTypeForm> getSensordatatypes() {
        return sensordatatypes;
    }

    public void setSensordatatypes(List<SensorDataTypeForm> sensordatatypes) {
        this.sensordatatypes = sensordatatypes;
    }
}
