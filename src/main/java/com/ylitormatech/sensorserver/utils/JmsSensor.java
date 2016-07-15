package com.ylitormatech.sensorserver.utils;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
public class JmsSensor {
    String apikey;
    Integer id;
    String datatype;

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

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}
