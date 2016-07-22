package com.ylitormatech.sensorserver.domain.service.impl;

import com.ylitormatech.sensorserver.domain.entity.SensorDatatypeEntity;
import com.ylitormatech.sensorserver.domain.repository.SensorDatatypeRepository;
import com.ylitormatech.sensorserver.domain.repository.SensorRepository;
import com.ylitormatech.sensorserver.domain.service.SensorDatatypeService;
import com.ylitormatech.sensorserver.web.SensorDataTypeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * Created by marco on 17.7.2016.
 */
@Service("sensorDatatypeService")

public class SensorDatatypeServiceImpl implements SensorDatatypeService {

    @Autowired
    SensorDatatypeRepository sensorDatatypeRepository;

    @Transactional(readOnly = false)
    public List<SensorDatatypeEntity> findAll() {
        return sensorDatatypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean isDatatypesValid( List<SensorDataTypeForm> sensortypes) {
        boolean returnValue = true;
        for (SensorDataTypeForm entity:sensortypes) {
            if(!sensorDatatypeRepository.isDatatypeValid(entity.getName())){
                returnValue = false;
            }
        }
        return returnValue;
    }
}
