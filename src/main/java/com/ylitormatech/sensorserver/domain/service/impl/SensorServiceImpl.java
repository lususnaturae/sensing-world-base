package com.ylitormatech.sensorserver.domain.service.impl;

import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.repository.SensorDatatypeRepository;
import com.ylitormatech.sensorserver.domain.repository.SensorRepository;
import com.ylitormatech.sensorserver.domain.service.SensorService;
import com.ylitormatech.sensorserver.utils.ApiKeyGenerator;
import com.ylitormatech.sensorserver.web.SensorDataTypeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Marco Ylitörmä on 02/05/16.
 */
@Service("sensorService")
public class SensorServiceImpl implements SensorService{

    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    SensorDatatypeRepository sensorDatatypeRepository;

    @Transactional(readOnly = false)
    public SensorEntity restAdd(String name, List<SensorDataTypeForm> sensortypes, Integer userId) {

        SensorEntity sensorEntity = new SensorEntity();
        Set<SensorDatatypeEntity> sensorDatatypeEntityList = new HashSet<SensorDatatypeEntity>();

        for (SensorDataTypeForm entity:sensortypes) {
            sensorDatatypeEntityList.add(sensorDatatypeRepository.findByName(entity.getName()));
        }

        sensorEntity.setName(name);
        sensorEntity.setSensorDatatypeEntities(sensorDatatypeEntityList);// setSensorDatatype(sensorDatatypeEntityList);
        sensorEntity.setUserid(userId);
        sensorEntity.setApikey(new ApiKeyGenerator().createNewApiKey(name));
        sensorRepository.add(sensorEntity);
        return sensorEntity;
    }

    @Transactional(readOnly = true)
    public boolean restFindExist(Integer id, Integer userId){
        return sensorRepository.restFindIdUserIdExist(id,userId);
    }

    @Transactional(readOnly = true)
    public boolean restFindIsSensorNameExist(String name, Integer userId) {
        return sensorRepository.restFindNameUserIdExist(name, userId);
    }

    @Transactional(readOnly = true)
    public boolean restFindIsSensorNameEquals(String name, Integer id, Integer userId) {
        SensorEntity sensorEntity = sensorRepository.findMySensor(id,userId);
        if(sensorEntity.getName().equals(name)){
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean restIsSentToOnLog(Integer id, Integer userId) {
        SensorEntity sensorEntity = sensorRepository.findMySensor(id, userId);
        return sensorEntity.isOnLogSend();
    }

    @Transactional(readOnly = true)
    public SensorEntity restFind(Integer id, Integer userId){
        return sensorRepository.restFindIdUserId(id, userId);
    }

    @Transactional(readOnly = true)
    public SensorEntity findMySensor(Integer id, Integer userid) {
        return sensorRepository.findMySensor(id, userid);
    }

    @Transactional(readOnly = false)
    public void update(String name, List<SensorDataTypeForm> sensortypes, Integer id, Integer userId) {
        SensorEntity s = sensorRepository.findMySensor(id,userId);

        Set<SensorDatatypeEntity> sensorDatatypeEntityList = new HashSet<SensorDatatypeEntity>();
        for (SensorDataTypeForm entity: sensortypes) {
            sensorDatatypeEntityList.add(sensorDatatypeRepository.findByName(entity.getName()));
        }

        s.getSensorDatatypeEntities().clear();
        s.setName(name);
        s.setSensorDatatypeEntities(sensorDatatypeEntityList);
        sensorRepository.update(s);
    }


    @Transactional(readOnly = false)
    public void restUpdateOnlog(SensorEntity sensorEntity) {
        sensorEntity.setOnLogSend(true);
        sensorRepository.update(sensorEntity);
    }

    @Transactional(readOnly = false)
    public void removeMySensor(Integer id, Integer userid) {
        sensorRepository.removeMySensor(id, userid);
    }

    @Transactional(readOnly = true)
    public List<SensorEntity> restFindAll(Integer userId){
            return sensorRepository.restFindAll(userId);
    }

    @Transactional(readOnly = true)
    public List<SensorEntity> onLogNotSendList() {return  sensorRepository.onLogNotSendList();}
}
