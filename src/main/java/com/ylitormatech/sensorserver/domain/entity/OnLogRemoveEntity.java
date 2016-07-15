package com.ylitormatech.sensorserver.domain.entity;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
@Entity
public class OnLogRemoveEntity {
    @Id
    @GeneratedValue
    Integer id;

    Integer sensorId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }
}
