package com.example.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // UnsercureServerTest shall pass
//        http.authorizeHttpRequests((authz) -> authz.anyRequest().permitAll());

        // Authentication
        http.authorizeHttpRequests((authz) -> authz.anyRequest().authenticated())
                .httpBasic(withDefaults())

                // Partially working: second connection rejected but cannot reconnect after logout
                .sessionManagement((sessions) -> sessions
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                );

                // Not working: user can connect any amount of times
//                .sessionManagement((sessions) -> sessions
//                        .sessionConcurrency((concurrency) -> concurrency
//                                .maximumSessions(1)
//                                .maxSessionsPreventsLogin(true)
//                        )
//                );

        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
