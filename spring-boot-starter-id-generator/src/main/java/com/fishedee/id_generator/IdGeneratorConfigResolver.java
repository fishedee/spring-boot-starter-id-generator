package com.fishedee.id_generator;

public interface IdGeneratorConfigResolver {
    PersistConfig get(String key);
}
