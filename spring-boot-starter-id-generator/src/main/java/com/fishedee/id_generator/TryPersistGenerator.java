package com.fishedee.id_generator;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Queue;
import java.util.Stack;

public class TryPersistGenerator implements TryIdGenerator{

    @Autowired
    private CurrentTime currentTime;

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
    public String getPeek(String key){
        TryPersistCounter counter = this.getCounter(key);
        return counter.peek();
    }

    @Override
    public void moveNext(String key){
        TryPersistCounter counter = this.getCounter(key);
        counter.next();
    }
}
