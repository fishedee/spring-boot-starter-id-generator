package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.PersistConfig;
import com.fishedee.id_generator.PersistConfigRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistConfigRepositoryStub implements PersistConfigRepository {
    private Map<String,PersistConfig> mapConfig = new HashMap<>();

    public PersistConfig get(String key) {
        return mapConfig.get(key);
    }

    public PersistConfig getForUpdate(String key) {
        return mapConfig.get(key);
    }

    public void set(String key,PersistConfig config) {
        PersistConfig oldConfig = mapConfig.get(key);
        if( oldConfig == null ){
            mapConfig.put(key,config);
        }else{
            oldConfig.setInitialValue(config.getInitialValue());
        }
    }

    public void clear(){
        mapConfig.clear();
    }
}
