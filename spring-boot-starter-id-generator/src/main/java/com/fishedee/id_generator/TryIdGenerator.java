package com.fishedee.id_generator;

public interface TryIdGenerator {
    String getPeek(String key);

    void moveNext(String key);
}
