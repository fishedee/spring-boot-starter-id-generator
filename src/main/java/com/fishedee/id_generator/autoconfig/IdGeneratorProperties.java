package com.fishedee.id_generator.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="spring.id-generator")
public class IdGeneratorProperties {
    private boolean enable;

    private String table = "id_generator_config";

    private String tryTable = "try_id_generator_config";

    private String numberTable = "number_id_generator_config";

    private String beginEscapeCharacter = "`";

    private String endEscapeCharacter = "`";
}
