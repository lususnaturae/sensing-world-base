package com.ylitormatech.sensorserver.domain.repository;

import com.ylitormatech.sensorserver.domain.entity.OnLogRemoveEntity;

import java.util.List;

/**
 * Created by Perttu Vanharanta on 15.7.2016.
 */
public interface OnLogRemoveRepository {
    public void addRemoved(OnLogRemoveEntity onLogRemoveEntity);
    public List<OnLogRemoveEntity> findRemoved();
    public void removeRemoved(OnLogRemoveEntity onLogRemoveEntity);
}
