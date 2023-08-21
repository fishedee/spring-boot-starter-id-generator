package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.*;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootConfiguration
public class MyConfig {
    @Bean
    @Primary
    public CurrentTime getCurrentTime() {
        return new CurrentTimeStub();
    }

    @Bean
    @Primary
    public PersistConfigRepository getPersistConfigRepository(){
        return new PersistConfigRepositoryStub();
    }

    @Bean
    @Primary
    public TryPersistRepository getTryPersistRepository(){return new TryPersistRepositoryStub();}

    @Bean
    @Primary
    public PersistCounterGenerator persistCounterGenerator(PersistConfigRepository persistConfigRepository,CurrentTime currentTime){
        return new PersistCounterGenerator(persistConfigRepository,currentTime);
    }


    @Bean
    @Primary
    public TryPersistGenerator tryPersistCounterGenerator(){
        return new TryPersistGenerator();
    }

    @Bean
    @Primary
    public PersistGenerator persistGenerator(PersistCounterGenerator counterGenerator,PersistConfigRepository configRepository){return new PersistGenerator(counterGenerator,configRepository);}

}
