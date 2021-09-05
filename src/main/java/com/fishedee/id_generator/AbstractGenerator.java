package com.fishedee.id_generator;

import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGenerator implements IdGenerator{
    private Map<Class,String> classToKey;

    private Map<String,Class> keyMap;

    public AbstractGenerator(){
        classToKey = new HashMap<>();
        keyMap = new HashMap<>();
    }

    private String extractClassKey(Class clazz){
        IdGeneratorKey idGeneratorKey = (IdGeneratorKey)clazz.getAnnotation(IdGeneratorKey.class);
        if( idGeneratorKey == null ){
            throw new RuntimeException(clazz.getName()+"缺少@IdGeneratorKey注解");
        }
        String keyStr = idGeneratorKey.value().trim();
        if(Strings.isBlank(keyStr)){
            throw new RuntimeException(clazz.getName()+"@IdGeneratorKey 参数为空");
        }
        if( keyMap.containsKey(keyStr) ){
            throw new RuntimeException(clazz.getName()+"与"+keyMap.get(keyStr)+"的ID值相同!");
        }
        keyMap.put(keyStr,clazz);
        return keyStr;
    }

    private String getClassKey(Class clazz){
        String key = classToKey.get(clazz);
        if( key != null ){
            return key;
        }
        key = extractClassKey(clazz);
        classToKey.put(clazz,key);
        return key;
    }

    @Override
    public String getKey(Object instance){
        return getClassKey(instance.getClass());
    }
    
    @Override
    public Long nextLong(Object instance){
        String id = this.next(instance);
        try{
            Long value = Long.valueOf(id);
            return value;
        }catch(NumberFormatException e){
            throw new IdGeneratorException(1,"ID不能转换为整数["+id+"]",null);
        }
    }

    @Override
    public String next(Object instance){
        String key = getClassKey(instance.getClass());
        return this.next(key);
    }

    @Override
    public Long nextLong(String key){
        String id = this.next(key);
        try{
            Long value = Long.valueOf(id);
            return value;
        }catch(NumberFormatException e){
            throw new IdGeneratorException(1,"ID不能转换为整数["+id+"]",null);
        }
    }

    //这个交给子类来实现
    @Override
    public abstract String next(String key);
}
