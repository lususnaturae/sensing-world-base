package com.ylitormatech.sensorserver.domain.service;

import org.springframework.messaging.Message;

/**
 * Created by Perttu Vanharanta on 7.6.2016.
 */
public interface MessageService {

    public String sendMessage(Message<String> message);
    public String sendMessageInt(Message<Integer> message);

}
