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
    @ConditionalOnMissingBean(TenantResolver.class)
    public TenantResolver tenantResolver(){
        return new DefaultTenantResolver();
    }

    @Bean
    @ConditionalOnMissingBean(PersistConfigRepository.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public PersistConfigRepository persistConfigRepository(){
        return new PersistConfigRepositoryJdbc(this.properties.getTable(),
                this.properties.getBeginEscapeCharacter(),
                this.properties.getEndEscapeCharacter());
    }

    @Bean
    @ConditionalOnMissingBean(PersistCounterGenerator.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public PersistCounterGenerator persistCounterGenerator(PersistConfigRepository persistConfigRepository,CurrentTime currentTime){
        return new PersistCounterGenerator(persistConfigRepository,currentTime);
    }

    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public IdGenerator idGenerator(PersistCounterGenerator counterGenerator,PersistConfigRepository persistConfigRepository,TenantResolver tenantResolver){
        return new PersistGenerator(counterGenerator,persistConfigRepository,tenantResolver);
    }

    @Bean
    @ConditionalOnMissingBean(IdGeneratorConfigResolver.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public IdGeneratorConfigResolver idGeneratorConfigResolver(){
        return new IdGeneratorConfigResolverImpl();
    }

    @Bean
    @ConditionalOnMissingBean(TryPersistRepository.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public TryPersistRepository tryPersistRepository(){
        return new TryPersistRepositoryJdbc(this.properties.getTryTable(),
                this.properties.getBeginEscapeCharacter(),
                this.properties.getEndEscapeCharacter());
    }

    @Bean
    @ConditionalOnMissingBean(TryPersistGenerator.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public TryPersistGenerator tryPersistGenerator(){
        return new TryPersistGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(TryIdGeneratorConfigResolver.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public TryIdGeneratorConfigResolver tryIdGeneratorConfigResolver(){
        return new TryIdGeneratorConfigResolverImpl();
    }

    @Bean
    @ConditionalOnMissingBean(NumberPersistRepository.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public NumberPersistRepository numberPersistRepository(){
        return new NumberPersistRepositoryJdbc(this.properties.getNumberTable(),
                this.properties.getBeginEscapeCharacter(),
                this.properties.getEndEscapeCharacter());
    }

    @Bean
    @ConditionalOnMissingBean(NumberPersistGenerator.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public NumberPersistGenerator numberPersistGenerator(){
        return new NumberPersistGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(NumberGeneratorConfigResolver.class)
    @ConditionalOnProperty(value = "spring.id-generator.enable", havingValue = "true")
    public NumberGeneratorConfigResolver numberGeneratorConfigResolver(){
        return new NumberGeneratorConfigResolverImpl();
    }
}
