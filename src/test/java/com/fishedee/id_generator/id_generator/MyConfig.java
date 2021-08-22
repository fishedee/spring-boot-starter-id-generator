package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.CurrentTime;
import com.fishedee.id_generator.PersistConfigRepository;
import com.fishedee.id_generator.PersistCounterGenerator;
import com.fishedee.id_generator.PersistGenerator;
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
    public PersistCounterGenerator persistCounterGenerator(){return new PersistCounterGenerator();}

    @Bean
    @Primary
    public PersistGenerator persistGenerator(PersistCounterGenerator counterGenerator){return new PersistGenerator(counterGenerator);}

}
