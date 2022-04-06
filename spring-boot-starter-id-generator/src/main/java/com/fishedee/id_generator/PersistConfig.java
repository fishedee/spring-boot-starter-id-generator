package com.fishedee.id_generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersistConfig {
    private String key;

    private String template;

    private int step;

    private String initialValue;

    private Byte isSync;
}
