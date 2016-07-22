package com.ylitormatech.sensorserver.domain.entity;
/**
 * Created by marco on 17.7.2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany
    @JsonIgnore
    Set<SensorEntity> sensorEntities = new HashSet<SensorEntity>();

    public Set<SensorEntity> getSensorEntities() {
        return sensorEntities;
    }

    public void setSensorEntities(Set<SensorEntity> sensorEntities) {
        this.sensorEntities = sensorEntities;
    }

    /*
        public List<SensorEntity> getSensorEntities() {
            return sensorEntities;
        }

        public void setSensorEntities(List<SensorEntity> sensorEntities) {
            this.sensorEntities = sensorEntities;
        }
    */
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
