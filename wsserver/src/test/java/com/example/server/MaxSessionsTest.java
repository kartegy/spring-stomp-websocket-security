package com.example.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaxSessionsTest {

    @Value(value = "${local.server.port}")
    private int port;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @BeforeEach
    public void setup() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(webSocketClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void shouldNotAllowMultipleConnections() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new TestSessionHandler(failure);

        // Basic HTTP authentication
        String userData = "aValidUser" + ":" + "secret";
        String auth = "Basic " + Base64.getEncoder().encodeToString(userData.getBytes());
        this.headers.add(HttpHeaders.AUTHORIZATION, auth);

        CompletableFuture<StompSession> future =
                this.stompClient.connectAsync("ws://localhost:{port}/myEndpoint", this.headers, handler, this.port);

//        future.get().disconnect();

        Thread.sleep(2000);

        future = this.stompClient.connectAsync("ws://localhost:{port}/myEndpoint", this.headers, handler, this.port);

        try {
            future.get();
            fail("User have been able to connect 2 times");
        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReAuthenticateAfterDisconnection() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new TestSessionHandler(failure);

        // Basic HTTP authentication
        String userData = "aValidUser" + ":" + "secret";
        String auth = "Basic " + Base64.getEncoder().encodeToString(userData.getBytes());
        this.headers.add(HttpHeaders.AUTHORIZATION, auth);

        CompletableFuture<StompSession> future =
                this.stompClient.connectAsync("ws://localhost:{port}/myEndpoint", this.headers, handler, this.port);

        future.get().disconnect();

        Thread.sleep(2000);

        future = this.stompClient.connectAsync("ws://localhost:{port}/myEndpoint", this.headers, handler, this.port);

        try {
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestSessionHandler extends StompSessionHandlerAdapter {

        private final AtomicReference<Throwable> failure;

        public TestSessionHandler(AtomicReference<Throwable> failure) {
            this.failure = failure;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
        }
    }
}
