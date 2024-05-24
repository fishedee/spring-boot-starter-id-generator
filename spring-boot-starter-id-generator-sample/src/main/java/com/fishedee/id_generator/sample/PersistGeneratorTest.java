package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class PersistGeneratorTest {

    @Autowired
    private IdGenerator idGenerator;

    @Transactional
    public String nextKeyWithThrowControlInner(String key,boolean shouldThrow){
        String mm = idGenerator.next(key);
        if( shouldThrow ){
            throw new RuntimeException("test");
        }
        return mm;
    }

    public void nextKeyWithThrowControl(String key,boolean shouldThrow){
        PersistGeneratorTest app = (PersistGeneratorTest) AopContext.currentProxy();
        try{
            String nextId = app.nextKeyWithThrowControlInner(key,shouldThrow);
            log.info("{} {}",key,nextId);
        }catch(Exception e){
            log.info("{} throw",key);
        }
    }


    @Transactional
    public void testUpdateSyncAndAsyncInner(String key,String key2){
        idGenerator.next(key);
        idGenerator.next(key2);
    }

    public void testUpdateSyncAndAsync(){
        String orderKey2 = "order.purchase_order";
        String orderKey = "order.sales_order";
        PersistGeneratorTest app = (PersistGeneratorTest)AopContext.currentProxy();
        app.testUpdateSyncAndAsyncInner(orderKey2,orderKey);
    }

    public void runNormal() throws Exception{
        testUpdateSyncAndAsync();
        String userKey = "user.user";
        for( int i = 0 ;i != 10 ;i++){
            log.info("{} {}",userKey,idGenerator.next(userKey));
            log.info("{} {}",userKey,idGenerator.next(new User()));
        }
        log.info("{} {}",idGenerator.getKey(User.class),idGenerator.getName(User.class));
        log.info("{} {}",idGenerator.getKey(new User()),idGenerator.getName(new User()));

        String orderKey = "order.sales_order";
        for( int i = 0 ;i != 10 ;i++){
            log.info("{} {}",orderKey,idGenerator.next(orderKey));
            log.info("{} {}",orderKey,idGenerator.next(new SalesOrder()));
        }
        log.info("{} {}",idGenerator.getKey(SalesOrder.class),idGenerator.getName(SalesOrder.class));
        log.info("{} {}",idGenerator.getKey(new SalesOrder()),idGenerator.getName(new SalesOrder()));


        //SalesOrder无同步功能，会有ID间隙，但是性能更好
        for( int i = 0 ;i != 20 ;i++){
            this.nextKeyWithThrowControl(orderKey,i%3==0);
        }

        //SalesOrder有同步功能，保证没有ID间隙，但是每次ID都需要到数据库拿，性能不太好
        String orderKey2 = "order.purchase_order";
        for( int i = 0 ;i != 20 ;i++){
            this.nextKeyWithThrowControl(orderKey2,i%3==0);
        }
    }

    public void runWithClearCache() throws Exception {
        String orderKey = "order.sales_order";
        for( int i = 0 ;i != 5 ;i++){
            log.info("{} {}",orderKey,idGenerator.next(new SalesOrder()));
        }
        //清除所有的cache
        idGenerator.clearCache(false);
        for( int i = 0 ;i != 5 ;i++){
            log.info("{} {}",orderKey,idGenerator.next(new SalesOrder()));
        }
        //清除所有的cache，租户
        idGenerator.clearCache(true);
        for( int i = 0 ;i != 5 ;i++){
            log.info("{} {}",orderKey,idGenerator.next(new SalesOrder()));
        }


        String orderKey2 = "order.stock_order";
        for( int i = 0 ;i != 5 ;i++){
            log.info("{} {}",orderKey2,idGenerator.next(new StockOrder()));
        }
        idGenerator.clearCache(false);
        for( int i = 0 ;i != 5 ;i++){
            log.info("{} {}",orderKey2,idGenerator.next(new StockOrder()));
        }
    }

    @Transactional
    public void runWithTransactional() throws Exception{
        String orderKey2 = "order.cash_order";
        for( int i = 0 ;i != 5 ;i++){
            log.info("{} {}",orderKey2,idGenerator.next(new CashOrder()));
        }
    }
}
