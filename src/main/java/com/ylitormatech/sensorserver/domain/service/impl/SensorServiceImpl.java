package com.ylitormatech.sensorserver.domain.service.impl;

import com.ylitormatech.sensorserver.domain.entity.SensorEntity;
import com.ylitormatech.sensorserver.domain.repository.SensorRepository;
import com.ylitormatech.sensorserver.domain.service.SensorService;
import com.ylitormatech.sensorserver.utils.ApiKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco Ylitörmä on 02/05/16.
 */
@Service("sensorService")
@Transactional
public class SensorServiceImpl implements SensorService{

    @Autowired
    SensorRepository sensorRepository;


    public SensorEntity restAdd(String name, String usage, Integer userId) {

        SensorEntity sensorEntity = new SensorEntity();
        sensorEntity.setName(name);
        sensorEntity.setUsagetoken(usage);
        sensorEntity.setUserid(userId);
        sensorEntity.setApikey(new ApiKeyGenerator().createNewApiKey(name));
        sensorRepository.add(sensorEntity);
        return sensorEntity;
    }

    public boolean restFindExist(Integer id, Integer userId){
        return sensorRepository.restFindIdUserIdExist(id,userId);
    }
    public SensorEntity restFind(Integer id, Integer userId){
        return sensorRepository.restFindIdUserId(id, userId);
    }
    public SensorEntity findMySensor(Integer id, Integer userid) {
        return sensorRepository.findMySensor(id, userid);
    }

    public void update(String name, String usage, Integer id, Integer userId) {
        SensorEntity s = sensorRepository.findMySensor(id,userId);
        s.setName(name);
        s.setUsagetoken(usage);
        sensorRepository.update(s);
    }

    public void removeMySensor(Integer id, Integer userid) {
        sensorRepository.removeMySensor(id, userid);
    }

    public List<SensorEntity> restFindAll(Integer userId){
            return sensorRepository.restFindAll(userId);
    }
}
