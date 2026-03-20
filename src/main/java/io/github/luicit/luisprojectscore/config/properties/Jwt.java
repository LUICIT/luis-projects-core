package io.github.luicit.luisprojectscore.config.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Jwt {

    private String secret;
    private long expiration;

}
