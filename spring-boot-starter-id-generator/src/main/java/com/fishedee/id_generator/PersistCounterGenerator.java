package com.fishedee.id_generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class PersistCounterGenerator {
    @Autowired
    private PersistConfigRepository persistConfigRepository;

    @Autowired
    private CurrentTime currentTime;

    //REQUIRES_NEW让id不随外部事务提交而变化
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = RuntimeException.class)
    public PersistCounter getCounterAsync(String key){
        return this.getCounterInner(key);
    }

    public PersistCounter getCounterSync(String key){
        if(TransactionSynchronizationManager.isActualTransactionActive() == false ){
            throw new RuntimeException("没有开启事务的情况不能使用TransactionSynchronizationManager");
        }
        return this.getCounterInner(key);
    }


    private PersistCounter getCounterInner(String key){
        //这一句有隐式的同key下的for update锁
        PersistConfig config = persistConfigRepository.get(key);

        //用新值来赋予counter
        PersistCounter counter = new PersistCounter(currentTime,config);

        //释放同key的for update锁
        persistConfigRepository.set(key,counter.getNextConfig());

        return counter;
    }
}
