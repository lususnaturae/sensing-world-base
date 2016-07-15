package com.ylitormatech.sensorserver.domain.service;

import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;

import java.util.List;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
public interface OnLogRemoveService {
    public void addRemove(Integer id);
    public List<OnLogRemoveEntity> findRemoveList();
    public void deleteRemove(OnLogRemoveEntity onLogRemoveEntity);
}
