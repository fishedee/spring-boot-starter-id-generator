package com.fishedee.id_generator;

import java.util.Date;

public interface NumberGenerator {
    NumberCounter get(Date dateTime, String key);
}
