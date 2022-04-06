package com.fishedee.id_generator.autoconfig;

import com.fishedee.id_generator.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Slf4j
@Configuration
@EnableConfigurationProperties(IdGeneratorProperties.class)
public class IdGeneratorAutoConfiguration {
    private final AbstractApplicationContext applicationContext;

    private final IdGeneratorProperties properties;

    public IdGeneratorAutoConfiguration(AbstractApplicationContext applicationContext, IdGeneratorProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(CurrentTime.class)
    public CurrentTime currentTime() {
        return new DefaultCurrentTime();
    }

    @Bean
    @ConditionalOnMissingBean(PersistConfigRepository.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public PersistConfigRepository persistConfigRepository(){
        return new PersistConfigRepositoryJdbc(this.properties.getTable());
    }

    @Bean
    @ConditionalOnMissingBean(PersistCounterGenerator.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public PersistCounterGenerator persistCounterGenerator(){
        return new PersistCounterGenerator();
    }


    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public IdGenerator idGenerator(PersistCounterGenerator counterGenerator,PersistConfigRepository persistConfigRepository){
        return new PersistGenerator(counterGenerator,persistConfigRepository);
    }

}
