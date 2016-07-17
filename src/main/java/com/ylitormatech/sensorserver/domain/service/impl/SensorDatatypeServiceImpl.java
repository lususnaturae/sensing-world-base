package com.ylitormatech.sensorserver.domain.service.impl;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.repository.SensorDatatypeRepository;
import com.ylitormatech.sensorserver.domain.repository.SensorRepository;
import com.ylitormatech.sensorserver.domain.service.SensorDatatypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by marco on 17.7.2016.
 */
@Service("sensorDatatypeService")
@Transactional
public class SensorDatatypeServiceImpl implements SensorDatatypeService {

    @Autowired
    SensorDatatypeRepository sensorDatatypeRepository;

    public List<SensorDatatypeEntity> findAll() {
        return sensorDatatypeRepository.findAll();
    }
}
