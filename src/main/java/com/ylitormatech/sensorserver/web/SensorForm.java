package com.ylitormatech.sensorserver.web;

import org.hibernate.validator.constraints.NotEmpty;


/**
 * Created by Marco Ylitörmä on 03/05/16.
 */
public class SensorForm {

    @NotEmpty
    String name;
    @NotEmpty
    String usagetoken;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsagetoken() {
        return usagetoken;
    }

    public void setUsagetoken(String usagetoken) {
        this.usagetoken = usagetoken;
    }

}
