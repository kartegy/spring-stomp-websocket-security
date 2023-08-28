package com.example.client;

import com.example.MyMessage;
import jakarta.websocket.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class App {

  final static Logger logger = LogManager.getLogger(App.class);

  public static void main(String[] args) throws InterruptedException {

    WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
    WebSocketClient client = new StandardWebSocketClient(webSocketContainer);
    WebSocketStompClient stompClient = new WebSocketStompClient(client);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    StompSessionHandler sessionHandler = new SessionHandler();

    WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
    StompHeaders stompHeaders = null;

    // Basic HTTP authentication
    String userData = "aValidUser" + ":" + "secret";

    String auth = "Basic " + Base64.getEncoder().encodeToString(userData.getBytes());
    httpHeaders.add("Authorization", auth);

    String endpointUrl = "ws://localhost:8080/myEndpoint";

    CompletableFuture<StompSession> newSession =
            stompClient.connectAsync(endpointUrl, httpHeaders, stompHeaders, sessionHandler);

    StompSession session;
    try {
      session = newSession.get();
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }

    try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
      String userInput;
      while (true) {

        userInput = stdIn.readLine();

        if (userInput != null) {

          if (userInput.contentEquals("exit")) {

            session.disconnect();

            logger.info("Exiting...bye");
            System.exit(0);
          }

          // Prepares the query for the server
          MyMessage q = new MyMessage();
          q.setContent(userInput);

          session.send("/app/hello", q);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
