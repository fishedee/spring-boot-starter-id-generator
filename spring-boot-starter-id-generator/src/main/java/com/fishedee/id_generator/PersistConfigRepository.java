package com.fishedee.id_generator;


import java.util.List;

public interface PersistConfigRepository {

    PersistConfig getForUpdate(String key);

    PersistConfig get(String key);

    void set(String key,PersistConfig config);
}
