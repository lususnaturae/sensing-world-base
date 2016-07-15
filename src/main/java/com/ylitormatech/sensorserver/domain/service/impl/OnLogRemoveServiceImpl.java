package com.ylitormatech.sensorserver.domain.service.impl;

import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;
import com.ylitormatech.sensorserver.domain.repository.OnLogRemoveRepository;
import com.ylitormatech.sensorserver.domain.service.OnLogRemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
@Service
public class OnLogRemoveServiceImpl implements OnLogRemoveService{

    @Autowired
    OnLogRemoveRepository onLogRemoveRepository;

    @Transactional(readOnly = false)
    public void addRemove(Integer id) {
        OnLogRemoveEntity onLogRemoveEntity = new OnLogRemoveEntity();
        onLogRemoveEntity.setSensorId(id);
        onLogRemoveRepository.addRemoved(onLogRemoveEntity);
    }

    @Transactional(readOnly = true)
    public List<OnLogRemoveEntity> findRemoveList() {
        return onLogRemoveRepository.findRemoved();
    }

    @Transactional(readOnly = false)
    public void deleteRemove(OnLogRemoveEntity onLogRemoveEntity) {
        onLogRemoveRepository.removeRemoved(onLogRemoveEntity);
    }
}
