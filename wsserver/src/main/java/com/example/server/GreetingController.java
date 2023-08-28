package com.example.server;

import com.example.MyMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;


@Controller
public class GreetingController {

  Logger logger = LogManager.getLogger(GreetingController.class);

  @MessageMapping("/hello")
  @SendToUser("/topic/greetings")
  public MyMessage greeting(MyMessage myMessage) throws Exception {
    MyMessage response = new MyMessage();
    response.setContent("Server response: " + myMessage.getContent());
    return response;
  }

}
