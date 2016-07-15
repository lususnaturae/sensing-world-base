package com.ylitormatech.sensorserver.domain.repository.impl;

import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.repository.OnLogRemoveRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
@Repository
public class OnLogRemoveRepositoryImpl implements OnLogRemoveRepository{

    @PersistenceContext
    EntityManager em;


    public void addRemoved(OnLogRemoveEntity onLogRemoveEntity) {
        em.persist(onLogRemoveEntity);
    }

    public List<OnLogRemoveEntity> findRemoved() {
        List<OnLogRemoveEntity> list = em.createQuery("FROM OnLogRemoveEntity").getResultList();
        return list;
    }

    public void removeRemoved(OnLogRemoveEntity onLogRemoveEntity) {
        em.remove(em.contains(onLogRemoveEntity) ? onLogRemoveEntity : em.merge(onLogRemoveEntity));
    }
}
