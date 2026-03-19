package com.luisraguilar.luisprojectscore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteAuthorizationInitializer {

    @Bean
    public RouteAuthorizationConfig routeAuthorizationConfig() {
        return new RouteAuthorizationConfig();
    }
}