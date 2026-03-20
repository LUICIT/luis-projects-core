package io.github.luicit.luisprojectscore.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CoreProperties.class)
public class CoreConfiguration {
}