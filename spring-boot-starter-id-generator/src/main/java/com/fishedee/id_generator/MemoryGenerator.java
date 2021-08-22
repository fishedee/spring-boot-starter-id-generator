package com.fishedee.id_generator;

import java.util.HashMap;
import java.util.Map;

public class MemoryGenerator extends AbstractGenerator {

    private Map<String,Long> mapClass = new HashMap<>();

    @Override
    public String next(String key){
        Long initial = mapClass.get(key);
        Long nextVal = null;
        if( initial == null ){
            initial = 10001L;
        }
        nextVal = initial + 1;
        mapClass.put(key,nextVal);
        return initial.toString();
    }

    public void clear(){
        this.mapClass.clear();
    }
}
