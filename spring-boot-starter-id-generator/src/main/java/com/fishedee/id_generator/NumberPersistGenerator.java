package com.fishedee.id_generator;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class NumberPersistGenerator implements NumberGenerator{
    private Map<String,Map<String,NumberCounter>>

    @Autowired
    private TryPersistRepository repository;

    private TryPersistCounter getCounter(String key){
        TryPersistTransactionalLifecycle.TryCache cache = TryPersistTransactionalLifecycle.getTryCache(this.repository);
        TryPersistCounter counter = cache.get(key);
        if( counter == null ){
            TryPersist dbPersist = this.repository.getForUpdate(key);
            counter = new TryPersistCounter(this.currentTime, dbPersist);
            cache.put(key,counter);
        }
        return counter;
    }

    @Override
    public NumberCounter get(Date dateTime, String key) {
        //同步化代码，只有一个线程能进入这里
        synchronized (this) {
        }
    }
}
