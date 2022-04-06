package com.fishedee.id_generator;


import java.util.List;

public interface PersistConfigRepository {

    PersistConfig get(String key);

    void set(String key,PersistConfig config);
}
