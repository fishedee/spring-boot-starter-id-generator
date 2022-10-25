package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.TryPersistGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class TryPersistGeneratorTest {

    @Autowired
    private TryPersistGenerator tryPersistGenerator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testId(){
        for( int i = 0 ;i != 2;i++){
            String key1 = tryPersistGenerator.getPeek("try_id");
            log.info("id key = "+key1);
        }

        for( int i = 0 ;i != 8;i++){
            tryPersistGenerator.moveNext("try_id");
            String key1 = tryPersistGenerator.getPeek("try_id");
            log.info("id key = "+key1);
        }
    }

    @Transactional
    public void testOrderId(){
        for( int i = 0 ;i != 2;i++){
            String key1 = tryPersistGenerator.getPeek("try_order_id");
            log.info("order_id key = "+key1);
        }

        for( int i = 0 ;i != 95;i++){
            tryPersistGenerator.moveNext("try_order_id");
        }

        for( int i = 0 ;i != 10;i++){
            tryPersistGenerator.moveNext("try_order_id");
            String key1 = tryPersistGenerator.getPeek("try_order_id");
            log.info("order_id key = "+key1);
        }
    }

    public void run(){
        TryPersistGeneratorTest app = (TryPersistGeneratorTest) AopContext.currentProxy();
        log.info("--- test 1 ---");
        app.testId();

        log.info("--- test 2 ---");
        app.testId();

        log.info("--- test 3 ---");
        app.testOrderId();

        log.info("--- test 4 ---");
        app.testOrderId();
    }
}
