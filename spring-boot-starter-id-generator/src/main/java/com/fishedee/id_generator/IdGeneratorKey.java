package com.fishedee.id_generator;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IdGeneratorKey {
    String value();

    String name() default "";
}
