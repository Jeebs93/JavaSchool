package com.example.rehab.service;

import com.example.rehab.models.dto.EventDTO;
import com.example.rehab.models.enums.EventStatus;
import com.example.rehab.service.impl.EventServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DispatcherService {


  private EventServiceImpl eventService;

  private JmsTemplate jmsTemplate;

  @Autowired
  public DispatcherService(JmsTemplate jmsTemplate, @Lazy EventServiceImpl eventService) {
      this.eventService=eventService;
      this.jmsTemplate=jmsTemplate;
  }


  @Value("${jms.queue}")
  String jmsQueue;


  public void sendMessage(String message) {
      jmsTemplate.convertAndSend(jmsQueue,message);
      log.info("Message has been send");
  }

  public String getMessage() {
      List<EventDTO> eventsToday = eventService.findAllToday()
              .stream()
              .filter(eventDTO -> eventDTO.getEventStatus().equals(EventStatus.PLANNED))
              .collect(Collectors.toList());
      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      return gson.toJson(eventsToday);
  }

}
