package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.css.Counter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PersistGenerator extends AbstractGenerator{

    private Map<String,PersistCounter> counterMap;

    private PersistCounterGenerator persistCounterGenerator;

    public PersistGenerator(PersistCounterGenerator persistCounterGenerator){
        counterMap = new HashMap<>();
        this.persistCounterGenerator = persistCounterGenerator;
    }

    @Override
    //同步化代码，只有一个线程能进入这里
    public synchronized String next(String key){
        //获取counter
        PersistCounter counter = counterMap.get(key);
        if( counter == null || counter.hasNext() == false ){
            //本地没有counter，或者counter已经到了尽头
            counter = persistCounterGenerator.getCounter(key);
            counterMap.put(key,counter);
        }

        //获取counter的下一个值
        return counter.next();
    }
}
