package com.fishedee.id_generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PersistConfig {
    private String key;

    private String template;

    private int step;

    private String initialValue;

    private Byte isSync;

    public PersistConfig(PersistConfig old){
        this.key = old.key;
        this.template = old.template;
        this.step = old.step;
        this.initialValue = old.initialValue;
        this.isSync = old.isSync;
    }
}
