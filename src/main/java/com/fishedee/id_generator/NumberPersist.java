package com.fishedee.id_generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NumberPersist {

    private String key;

    private String template;

    public NumberPersist(NumberPersist old){
        this.key = old.key;
        this.template = old.template;
    }
}
