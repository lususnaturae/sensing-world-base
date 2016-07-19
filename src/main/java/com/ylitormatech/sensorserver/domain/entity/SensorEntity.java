package com.ylitormatech.sensorserver.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.core.style.ToStringCreator;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Marco Ylitörmä on 02/05/16.
 */
@Table(name = "sensors")
@Entity
public class SensorEntity {


    @Id
    @GeneratedValue
    Integer id;

    @Column(length = 40)
    String name;

    @Column
    Double lat;
    @Column
    Double lon;


    @ManyToMany(cascade =CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name="Team_Player",
            joinColumns=@JoinColumn(name="sensorId", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="datatypes_id", referencedColumnName="id"))


            /*name="TrainedMonkeys",
            joinColumns = @JoinColumn( name="trainer_id"),
            inverseJoinColumns = @JoinColumn( name="monkey_id")
    )*/
//    List<SensorDatatypeEntity> sensordatatypes;
    Set<SensorDatatypeEntity> sensorDatatypeEntities = new HashSet<SensorDatatypeEntity>();
    @Column(length = 100)
    String apikey;

    Integer userid;

    boolean active = true;
    boolean onLogSend = false;

    public SensorEntity() {
    }
/*
    public SensorEntity(String name, List<SensorDatatypeEntity> sensordatatypes) {
        this.name = name;
        this.sensordatatypes = sensordatatypes;
    }
*/
    public Integer getId() {
        return id;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
/*
    public List<SensorDatatypeEntity> getSensorDatatypes() {
        return sensordatatypes;
    }

    public void setSensorDatatype(List<SensorDatatypeEntity> sensortypes) {
        this.sensordatatypes = sensortypes;
    }
*/

    public Set<SensorDatatypeEntity> getSensorDatatypeEntities() {
        return sensorDatatypeEntities;
    }

    public void setSensorDatatypeEntities(Set<SensorDatatypeEntity> sensorDatatypeEntities) {
        this.sensorDatatypeEntities = sensorDatatypeEntities;
    }

    public boolean isNew() {
        return (this.id == null);
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOnLogSend() {
        return onLogSend;
    }

    public void setOnLogSend(boolean onLogSend) {
        this.onLogSend = onLogSend;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)

                .append("id", this.getId())
                .append("new", this.isNew())
                .append("name", this.getName())
                .toString();
    }
}
