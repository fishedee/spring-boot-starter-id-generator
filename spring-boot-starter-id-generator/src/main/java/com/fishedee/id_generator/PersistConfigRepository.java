package com.fishedee.id_generator;


public interface PersistConfigRepository {

    PersistConfig get(String key);

    void set(String key,PersistConfig config);
}