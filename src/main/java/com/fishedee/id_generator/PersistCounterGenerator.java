package com.fishedee.id_generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PersistCounterGenerator {
    @Autowired
    private PersistConfigRepository persistConfigRepository;

    @Autowired
    private CurrentTime currentTime;

    //REQUIRES_NEW让id不随外部事务提交而变化
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = RuntimeException.class)
    public PersistCounter getCounter(String key){
        //这一句有隐式的同key下的for update锁
        PersistConfig config = persistConfigRepository.get(key);

        System.out.println("go "+currentTime);
        //用新值来赋予counter
        PersistCounter counter = new PersistCounter(currentTime,config);

        //释放同key的for update锁
        persistConfigRepository.set(key,counter.getNextConfig());

        return counter;
    }
}
