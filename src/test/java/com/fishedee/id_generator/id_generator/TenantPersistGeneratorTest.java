package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TenantPersistGeneratorTest {
    @IdGeneratorKey("class1")
    public static class Class1{

    }
    @Autowired
    private CurrentTimeStub currentTimeStub;

    @Autowired
    private TenantResolverStub tenantResolverStub;

    @Autowired
    private PersistConfigRepositoryStub persistConfigRepositoryStub;

    @Autowired
    private PersistGenerator persistGenerator;

    @Autowired
    private PersistCounterGenerator persistCounterGenerator;

    @Test
    public void testIdOnlyTwoTenant(){
        currentTimeStub.setNow(2020,1,1);

        //fish租户
        tenantResolverStub.setTenantId("fish");
        persistConfigRepositoryStub.set("class1",new PersistConfig("class1","{id}",10,"1",(byte)0));

        //cat租户
        tenantResolverStub.setTenantId("cat");
        persistConfigRepositoryStub.set("class1",new PersistConfig("class1","{id}",10,"101",(byte)0));

        //使用一个生成器
        PersistGenerator generator = new PersistGenerator(persistCounterGenerator,persistConfigRepositoryStub,tenantResolverStub);

        //生成器，租户fish，拿到1-20的段
        tenantResolverStub.setTenantId("fish");
        for( int i = 1 ;i <= 20;i++){
            String next = generator.next(new Class1());
            assertEquals(i+"",next);
        }

        //生成器，租户cat，拿到101-120的段
        tenantResolverStub.setTenantId("cat");
        for( int i = 1 ;i <= 20;i++){
            String next = generator.next(new Class1());
            assertEquals((i+100)+"",next);
        }

        //生成器，租户fish，拿到21-40的段
        tenantResolverStub.setTenantId("fish");
        for( int i = 1 ;i <= 20;i++){
            String next = generator.next(new Class1());
            assertEquals((i+20)+"",next);
        }

        //生成器，租户cat，拿到121-140的段
        tenantResolverStub.setTenantId("cat");
        for( int i = 1 ;i <= 20;i++){
            String next = generator.next(new Class1());
            assertEquals((i+120)+"",next);
        }
    }

    @Test
    public void testTimeExpireTwoCounter(){
        currentTimeStub.setNow(2020,1,1);

        //fish租户，还没过期
        tenantResolverStub.setTenantId("fish");
        persistConfigRepositoryStub.set("class1",new PersistConfig("class1","WS{year}{month}{day}{id:4}",2,"WS202001012801",(byte)0));

        //cat租户，过期了
        tenantResolverStub.setTenantId("cat");
        persistConfigRepositoryStub.set("class1",new PersistConfig("class1","WS{year}{month}{day}{id:4}",2,"WS201901012801",(byte)0));

        //使用两个生成器
        PersistGenerator generator = new PersistGenerator(persistCounterGenerator,persistConfigRepositoryStub,tenantResolverStub);

        //租户fish
        tenantResolverStub.setTenantId("fish");
        for( int i = 1 ;i <= 9;i++){
            String next = generator.next(new Class1());
            assertEquals("WS20200101280"+i,next);
        }

        //租户cat
        tenantResolverStub.setTenantId("cat");
        for( int i = 1 ;i <= 5;i++){
            String next = generator.next(new Class1());
            assertEquals("WS20200101000"+i,next);
        }

        //时间突变，两个租户都到尽头
        currentTimeStub.setNow(2020,1,2);
        tenantResolverStub.setTenantId("fish");
        for( int i = 1 ;i <= 5;i++){
            String next = generator.next(new Class1());
            assertEquals("WS20200102000"+i,next);
        }

        tenantResolverStub.setTenantId("cat");
        for( int i = 1 ;i <= 5;i++){
            String next = generator.next(new Class1());
            assertEquals("WS20200102000"+i,next);
        }
    }
}
