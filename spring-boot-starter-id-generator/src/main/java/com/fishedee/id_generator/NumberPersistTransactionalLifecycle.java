package com.fishedee.id_generator;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

public class NumberPersistTransactionalLifecycle {

    public static class NumberPersistCache{
        private Map<String, Map<String,NumberCounter>> counterMap = new HashMap<>();

        private Map<String,NumberPersist> configMap = new HashMap<>();

        public NumberPersistCache(){
        }

        public NumberPersist getConfig(String key){
            return configMap.get(key);
        }

        public NumberCounter getCounter(String key,NumberCounter defaultCounter){
            Map<String,NumberCounter> keyCounters = counterMap.get(key);
            if( keyCounters == null ){
                keyCounters = new HashMap<>();
                counterMap.put(key,keyCounters);
            }
            String matchRegex = defaultCounter.getMatchIdRegex();
            NumberCounter oldCounter = keyCounters.get(matchRegex);
            if( oldCounter != null ){
                //优先使用oldCounter
                return oldCounter;
            }
            keyCounters.put(matchRegex,defaultCounter);
            configMap.put(key,defaultCounter.getConfig());
            return defaultCounter;
        }

    }

    public static NumberPersistCache getCache(){
        if( TransactionSynchronizationManager.isActualTransactionActive() == false ){
            throw new RuntimeException("事务未开启，不能使用TryPersist功能");
        }
        boolean hasNumberCache = TransactionSynchronizationManager.hasResource(NumberPersistCache.class);
        NumberPersistCache numberPersistCache;
        if( hasNumberCache == false ){
            numberPersistCache = initNumberPersistCache();
        }else{
            numberPersistCache = (NumberPersistCache)TransactionSynchronizationManager.getResource(NumberPersistCache.class);
        }
        return numberPersistCache;
    }

    private static NumberPersistCache initNumberPersistCache(){
        final NumberPersistCache numberCounterCache = new NumberPersistCache();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){

            //只有可以commit才会回调,在提交前回调
            @Override
            public void beforeCommit(boolean readOnly) {
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
                TransactionSynchronizationManager.unbindResourceIfPossible(NumberPersistCache.class);
            }
        });
        TransactionSynchronizationManager.bindResource(NumberPersistCache.class,numberCounterCache);
        return numberCounterCache;
    }
}
