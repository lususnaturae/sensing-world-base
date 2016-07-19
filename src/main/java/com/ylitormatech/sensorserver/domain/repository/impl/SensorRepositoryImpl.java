package com.ylitormatech.sensorserver.domain.repository.impl;

import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.repository.SensorRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Marco Ylitörmä on 02/05/16.
 */
@Repository("sensorRepository")

public class SensorRepositoryImpl implements SensorRepository{

    @PersistenceContext
    EntityManager em;

    public void add(SensorEntity sensorEntity) {
        em.persist(sensorEntity);
    }


    public boolean restFindIdUserIdExist(Integer id, Integer userId){
        List<SensorEntity> list = em.createQuery("FROM SensorEntity u WHERE u.id=:id AND u.userid=:userId")
                .setParameter("id",id)
                .setParameter("userId",userId)
                .getResultList();
        if(!list.isEmpty())
        {
            return true;
        }
        return false;
    }

    public SensorEntity restFindIdUserId(Integer id, Integer userId){
        if(restFindIdUserIdExist(id,userId)){
            return findMySensor(id,userId);
        }
        return null;
    }

    public SensorEntity findMySensor(Integer id, Integer userId) {
        Query query = em.createQuery("FROM SensorEntity WHERE id=:id AND userId=:userId");
        query.setParameter("id", id);
        query.setParameter("userId", userId);

        return (SensorEntity) query.getSingleResult();
    }

    public void update(SensorEntity sensorEntity) {
        em.merge(sensorEntity);
    }

    public void removeMySensor(Integer id, Integer userId) {
        /*Query query = em.createQuery("FROM SensorEntity WHERE id=:id and userId=:userId");
        query.setParameter("id", id);
        query.setParameter("userId", userId);*/
        SensorEntity sensorEntity= findMySensor(id, userId);
        sensorEntity.getSensorDatatypeEntities().clear();
        em.remove(sensorEntity);
    }

    public List<SensorEntity> restFindAll(Integer userId){
        List<SensorEntity> list = em.createQuery("FROM SensorEntity  WHERE userId=:userId")
                .setParameter("userId", userId)
                .getResultList();
        return list;
    }

    public List<SensorEntity> onLogNotSendList(){
        List<SensorEntity> list = em.createQuery("FROM SensorEntity  WHERE onLogSend=:onLogSend")
                .setParameter("onLogSend", false)
                .getResultList();
        return list;
    }

}
