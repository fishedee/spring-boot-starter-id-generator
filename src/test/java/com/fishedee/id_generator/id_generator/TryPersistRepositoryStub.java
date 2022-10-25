package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.TryPersist;
import com.fishedee.id_generator.TryPersistRepository;

import java.util.HashMap;
import java.util.Map;

public class TryPersistRepositoryStub implements TryPersistRepository {

    private Map<String,TryPersist> mapConfig = new HashMap<>();

    @Override
    public TryPersist getForUpdate(String key) {
        return mapConfig.get(key);
    }

    @Override
    public void set(String key,TryPersist config) {
        TryPersist oldConfig = mapConfig.get(key);
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
