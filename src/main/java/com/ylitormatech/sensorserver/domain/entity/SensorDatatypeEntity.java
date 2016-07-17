package com.ylitormatech.sensorserver.domain.entity;
/**
 * Created by marco on 17.7.2016.
 */

import javax.persistence.*;
import java.util.List;

@Table(name = "sensortype")
@Entity
public class SensorDatatypeEntity {

    @Id
    @GeneratedValue
    Integer id;

    @Column(length = 40)
    String name;

    @Column(length = 40)
    String labeltoken;

    public SensorDatatypeEntity() {}

    public SensorDatatypeEntity(String name, String labeltoken) {
        this.name = name;
        this.labeltoken = labeltoken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabeltoken() {
        return labeltoken;
    }

    public void setLabeltoken(String labeltoken) {
        this.labeltoken = labeltoken;
    }
}
