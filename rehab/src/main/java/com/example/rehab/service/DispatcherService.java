package com.example.rehab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.stereotype.Service;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
@Service
public class DispatcherService {

  JmsTemplate jmsTemplate;

  @Autowired
  public DispatcherService(JmsTemplate jmsTemplate) {
      this.jmsTemplate=jmsTemplate;
  }

  @Value("${jms.queue}")
  String jmsQueue;

  public void sendMessage(String message) {
      jmsTemplate.convertAndSend(jmsQueue,message);
      log.info("Message has been send");
  }

}
