package com.fishedee.id_generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TryPersist {

    private String key;

    private String template;

    private String initialValue;

    public TryPersist(TryPersist old){
        this.key = old.key;
        this.template = old.template;
        this.initialValue = old.initialValue;
    }
}
