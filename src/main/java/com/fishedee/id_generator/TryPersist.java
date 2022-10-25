package com.fishedee.id_generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TryPersist {

    private String key;

    private String template;

    private String initialValue;
}
