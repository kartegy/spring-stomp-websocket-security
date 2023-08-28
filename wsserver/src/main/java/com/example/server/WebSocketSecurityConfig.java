package com.example.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages
            // UnsercureServerTest will pass
//            .anyMessage().permitAll();

            // Implements a basic authorization
            .anyMessage().hasRole("USER");

//            .nullDestMatcher().authenticated()
//            .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
//            .simpSubscribeDestMatchers("/topic/*", "/user/**").hasRole("USER")
//            .simpDestMatchers("/app/**").hasRole("USER")
//            .simpTypeMatchers(SUBSCRIBE, MESSAGE).denyAll()
//            .anyMessage().denyAll();
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }
}
