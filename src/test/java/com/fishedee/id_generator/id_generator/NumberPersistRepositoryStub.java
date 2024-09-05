package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.NumberPersist;
import com.fishedee.id_generator.NumberPersistRepository;

import java.util.HashMap;
import java.util.Map;

public class NumberPersistRepositoryStub implements NumberPersistRepository {

    private Map<String, NumberPersist> mapConfig = new HashMap<>();

    @Override
    public NumberPersist get(String key) {
        return mapConfig.get(key);
    }

    public void set(String key,NumberPersist config) {
        NumberPersist oldConfig = mapConfig.get(key);
        if( oldConfig == null ){
            mapConfig.put(key,config);
        }
    }

    public void clear(){
        mapConfig.clear();
    }
}
