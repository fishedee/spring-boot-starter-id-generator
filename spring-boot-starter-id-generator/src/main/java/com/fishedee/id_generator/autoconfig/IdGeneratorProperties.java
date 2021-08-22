package com.fishedee.id_generator.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="spring.id-generator")
public class IdGeneratorProperties {
    private boolean enable;

    private String table = "id_generator_config";
}
