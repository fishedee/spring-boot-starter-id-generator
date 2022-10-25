package com.fishedee.id_generator;

public interface TryPersistRepository {

    TryPersist getForUpdate(String key);

    void set(String key,TryPersist config);
}
