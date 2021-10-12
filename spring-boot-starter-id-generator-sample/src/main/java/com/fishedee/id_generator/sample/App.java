package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@Slf4j
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }

    @Autowired
    private IdGenerator idGenerator;

    @PostConstruct
    public void init(){
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
    }
}
