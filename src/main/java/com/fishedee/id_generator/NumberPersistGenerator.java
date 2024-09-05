package com.fishedee.id_generator;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class NumberPersistGenerator implements NumberGenerator{

    @Autowired
    private NumberPersistRepository repository;

    @Override
    public NumberCounter get(Date dateTime, String key) {
        NumberPersistTransactionalLifecycle.NumberPersistCache cache = NumberPersistTransactionalLifecycle.getCache();

        //优先取缓存的config
        NumberPersist config = cache.getConfig(key);
        if( config == null ){
            config = this.repository.get(key);
        }

        //优先取缓存的counter
        NumberCounter counter = new NumberCounter(dateTime,config);
        return cache.getCounter(key,counter);
    }
}
