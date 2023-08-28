package com.example.client;

import com.example.MyMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class SessionHandler extends StompSessionHandlerAdapter {

  private Logger logger = LogManager.getLogger(SessionHandler.class);

  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    logger.info("New session established : " + session.getSessionId());
    session.subscribe("/user/topic/greetings", this);
    logger.info("Subscribed");
  }

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    MyMessage response = (MyMessage) payload;
    if (response != null) {
      logger.info("Response received: " + response.getContent());
    } else {
      for (Map.Entry<String, String> item : headers.toSingleValueMap().entrySet()) {
        System.out.println(item.getKey() + ": " + item.getValue());
      }
    }
  }

  @Override
  public void handleException(StompSession session, StompCommand command, StompHeaders headers,
      byte[] payload, Throwable exception) {
    logger.error("Got an exception", exception);
  }

  @Override
  public Type getPayloadType(StompHeaders headers) {
    return MyMessage.class;
  }
}
