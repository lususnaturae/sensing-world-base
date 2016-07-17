package com.ylitormatech.sensorserver.domain.repository.impl;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.repository.SensorDatatypeRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 17.7.2016.
 */
@Repository("sensorDatatypeRepository")
@Transactional
public class SensorDatatypeRepositoryImpl  implements SensorDatatypeRepository{

    @PersistenceContext
    EntityManager em;

    public void inittypes() {
        add(new SensorDatatypeEntity("TMP", "sensor.type.checkbox.label.tmp"));
        add(new SensorDatatypeEntity("GPS", "sensor.type.checkbox.label.gps"));
        add(new SensorDatatypeEntity("FLAG", "sensor.type.checkbox.label.flag"));
        add(new SensorDatatypeEntity("HUM", "sensor.type.checkbox.label.hum"));
        add(new SensorDatatypeEntity("LUX", "sensor.type.checkbox.label.lux"));
    }


    public void add(SensorDatatypeEntity sensorDatatypeEntity) {
        em.persist(sensorDatatypeEntity);
    }

    public List<SensorDatatypeEntity> findAll(){
        List<SensorDatatypeEntity> list = em.createQuery("FROM SensorDatatypeEntity")
                .getResultList();
        if (list.size() == 0) {
            inittypes();
            list = em.createQuery("FROM SensorDatatypeEntity")
                    .getResultList();
        }
        return list;

    }
}
