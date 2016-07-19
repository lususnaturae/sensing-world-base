package com.ylitormatech.sensorserver.web;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ELY on 19.7.2016.
 */
public class SensorDataTypeForm {

    @NotEmpty
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
