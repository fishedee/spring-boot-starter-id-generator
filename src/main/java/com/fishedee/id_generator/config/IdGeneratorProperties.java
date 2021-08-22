package com.fishedee.id_generator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="spring.id-generator")
public class IdGeneratorProperties {
    private boolean enable;
}
