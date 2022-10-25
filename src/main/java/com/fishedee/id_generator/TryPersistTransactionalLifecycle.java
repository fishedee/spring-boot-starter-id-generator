package com.fishedee.id_generator;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

public class TryPersistTransactionalLifecycle {

    public static class TryCache{
        private Map<String, TryPersistCounter> data = new HashMap<>();

        private TryPersistRepository repository;

        public TryCache(TryPersistRepository repository){
            this.repository = repository;
        }

        public void put(String key,TryPersistCounter counter){
            this.data.put(key,counter);
        }

        public TryPersistCounter get(String key){
            return this.data.get(key);
        }

        public void flushToDb(){
            this.data.forEach((key,value)->{
                this.repository.set(key,value.getNextTry());
            });
        }
    }

    public static TryCache getTryCache(TryPersistRepository repository){
        if( TransactionSynchronizationManager.isActualTransactionActive() == false ){
            throw new RuntimeException("事务未开启，不能使用TryPersist功能");
        }
        boolean hasTryCache = TransactionSynchronizationManager.hasResource(TryCache.class);
        TryCache tryCache;
        if( hasTryCache == false ){
            tryCache = initTryCache(repository);
        }else{
            tryCache = (TryCache)TransactionSynchronizationManager.getResource(TryCache.class);
        }
        return tryCache;
    }

    private static TryCache initTryCache(TryPersistRepository repository){
        final TryCache tryCache = new TryCache(repository);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){

            //只有可以commit才会回调,在提交前回调
            @Override
            public void beforeCommit(boolean readOnly) {
                tryCache.flushToDb();
            }

            //只有可以commit才会回调,在提交后回调
            @Override
            public void afterCommit() {

            }

            //总是回调,没有参数,在完成前回调
            @Override
            public void beforeCompletion() {
            }

            //总是回调,status为0是提交成功,status为1是提交失败,在完成后回调
            @Override
            public void afterCompletion(int status) {
                //清理数据
                TransactionSynchronizationManager.unbindResourceIfPossible(TryCache.class);
            }
        });
        TransactionSynchronizationManager.bindResource(TryCache.class,tryCache);
        return tryCache;
    }
}
