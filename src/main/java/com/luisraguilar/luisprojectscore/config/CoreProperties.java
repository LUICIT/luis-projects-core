package com.luisraguilar.luisprojectscore.config;

import com.luisraguilar.luisprojectscore.config.properties.Database;
import com.luisraguilar.luisprojectscore.config.properties.Jwt;
import com.luisraguilar.luisprojectscore.config.properties.Security;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app")
public class CoreProperties {

    private Database database = new Database();
    private Jwt jwt = new Jwt();
    private Security security = new Security();

}
