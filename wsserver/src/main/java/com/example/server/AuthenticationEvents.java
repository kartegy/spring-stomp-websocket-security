package com.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEvents {

  final Logger logger = LogManager.getLogger(AuthenticationEvents.class);

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent success) {
    logger.info("AUTHENTICATION SUCCESS: " + success.toString());
  }

  @EventListener
  public void onFailure(AbstractAuthenticationFailureEvent failures) {
    logger.info("AUTHENTICATION FAILED: " + failures.toString());
  }
}
