package com.fishedee.id_generator;

public interface IdGenerator {
    String next(Object instance);

    Long nextLong(Object instance);

    String next(String key);

    Long nextLong(String key);
}
