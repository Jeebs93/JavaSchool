package com.example.rehab.service;

import com.example.rehab.service.impl.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiverService {

    private final EventServiceImpl eventService;

    private final DispatcherService dispatcherService;

    /**
     * Receives message from the queue and sends event list
     * @author Dmitriy Zhiburtovich
     */
    @JmsListener(destination="TestQueue")
    public void receiveMessage(String message) {
        dispatcherService.sendMessage(dispatcherService.getMessage());
    }


}

