package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(exposeProxy = true)
@Slf4j
public class App implements ApplicationRunner
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }

    @Autowired
    private PersistGeneratorTest persistGeneratorTest;

    @Autowired
    private TryPersistGeneratorTest tryPersistGeneratorTest;

    @Autowired
    private NumberGeneratorTest numberGeneratorTest;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        //persistGeneratorTest.runWithClearCache();

        //带事务的启动，测试死锁问题
        //persistGeneratorTest.runWithTransactional();

        //tryPersistGeneratorTest.run();

        numberGeneratorTest.run();
    }
}
