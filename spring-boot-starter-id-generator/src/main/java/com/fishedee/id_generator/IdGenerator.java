package com.fishedee.id_generator;

public interface IdGenerator {
    String getKey(Object instance);

    String getKey(Class clazz);

    String getName(Object instance);

    String getName(Class clazz);

    String next(Object instance);

    Long nextLong(Object instance);

    String next(String key);

    Long nextLong(String key);

    void clearCache(boolean isClearAllTenant);
}
