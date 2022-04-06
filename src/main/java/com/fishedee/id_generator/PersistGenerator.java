package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.omg.CORBA.Current;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.css.Counter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PersistGenerator extends AbstractGenerator{

    private Map<String,PersistCounter> counterMap;

    private PersistCounterGenerator persistCounterGenerator;

    private PersistConfigRepository persistConfigRepository;

    private Map<String,Boolean> syncCounterConfig;

    public PersistGenerator(PersistCounterGenerator persistCounterGenerator,PersistConfigRepository persistConfigRepository){
        counterMap = new HashMap<>();
        syncCounterConfig = new HashMap<>();
        this.persistCounterGenerator = persistCounterGenerator;
        this.persistConfigRepository = persistConfigRepository;
    }

    @Override
    //同步化代码，只有一个线程能进入这里
    public synchronized String next(String key){
        //获取同步配置
        Boolean isSync = syncCounterConfig.get(key);
        if( isSync == null ){
            PersistConfig config = persistConfigRepository.get(key);
            isSync = (config.getIsSync() == 1);
            syncCounterConfig.put(key,isSync);
        }

        //获取counter
        PersistCounter counter;
        if( isSync == true){
            counterMap.remove(key);
            counter = persistCounterGenerator.getCounterSync(key);
        }else{
            counter = counterMap.get(key);
            if( counter == null || counter.hasNext() == false ){
                //本地没有counter，或者counter已经到了尽头
                counter = persistCounterGenerator.getCounterAsync(key);
                counterMap.put(key,counter);
            }
        }

        //更新sync配置
        syncCounterConfig.put(key,counter.getIsSync());

        //获取counter的下一个值
        return counter.next();
    }
}