package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class PersistCounterGenerator {
    private PersistConfigRepository persistConfigRepository;

    private CurrentTime currentTime;

    public PersistCounterGenerator(PersistConfigRepository persistConfigRepository,CurrentTime currentTime){
        this.persistConfigRepository = persistConfigRepository;
        this.currentTime = currentTime;
    }

    //REQUIRES_NEW让id不随外部事务提交而变化
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = RuntimeException.class)
    public PersistCounter getCounterAsync(String key){
        return this.getCounterInner(key);
    }

    public PersistCounter getCounterSync(String key){
        if(TransactionSynchronizationManager.isActualTransactionActive() == false ){
            throw new RuntimeException("没有开启事务的情况不能使用idGenerator的getCounterSync同步功能");
        }
        return this.getCounterInner(key);
    }


    private PersistCounter getCounterInner(String key){
        //这一句有隐式的同key下的for update锁
        PersistConfig config = persistConfigRepository.getForUpdate(key);

        //用新值来赋予counter
        PersistCounter counter = new PersistCounter(currentTime,config);

        //释放同key的for update锁
        persistConfigRepository.set(key,counter.getNextConfig());

        return counter;
    }
}
