package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.IdGeneratorKey;
import com.fishedee.id_generator.PersistConfig;
import com.fishedee.id_generator.PersistCounterGenerator;
import com.fishedee.id_generator.PersistGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersistGeneratorTest {
    @IdGeneratorKey("class1")
    public static class Class1{

    }
    @Autowired
    private CurrentTimeStub currentTimeStub;

    @Autowired
    private PersistConfigRepositoryStub persistConfigRepositoryStub;

    @Autowired
    private PersistGenerator persistGenerator;

    @Autowired
    private PersistCounterGenerator persistCounterGenerator;

    @Test
    public void testIdOnly(){
        currentTimeStub.setNow(2020,1,1);
        persistConfigRepositoryStub.set("class1",new PersistConfig("{id}",10,"1"));

        for( int i = 1 ;i != 100;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals(i+"",next);
        }
    }

    @Test
    public void testIdOnlyTwoCounter(){
        currentTimeStub.setNow(2020,1,1);
        persistConfigRepositoryStub.set("class1",new PersistConfig("{id}",10,"1"));

        //使用两个生成器
        PersistGenerator generator1 = new PersistGenerator(persistCounterGenerator);
        PersistGenerator generator2 = new PersistGenerator(persistCounterGenerator);

        //生成器1，拿到1-10的段
        for( int i = 1 ;i <= 5;i++){
            String next = generator1.next(new Class1());
            assertEquals(i+"",next);
        }
        //生成器2，拿到11-20的段
        for( int i = 11 ;i <= 15;i++){
            String next = generator2.next(new Class1());
            assertEquals(i+"",next);
        }

        for( int i = 6 ;i <= 10;i++){
            String next = generator1.next(new Class1());
            assertEquals(i+"",next);
        }
        for( int i = 16 ;i <= 20;i++){
            String next = generator2.next(new Class1());
            assertEquals(i+"",next);
        }

        //生成器2先拿到下一个段
        for( int i = 21 ;i <= 25;i++){
            String next = generator2.next(new Class1());
            assertEquals(i+"",next);
        }

        for( int i = 31 ;i <= 35;i++){
            String next = generator1.next(new Class1());
            assertEquals(i+"",next);
        }
    }

    @Test
    public void testTime(){
        currentTimeStub.setNow(2020,1,1);
        persistConfigRepositoryStub.set("class1",new PersistConfig("WS{year}{month}{day}{id:4}",10,"WS202001012801"));

        for( int i = 1 ;i <= 9;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals("WS20200101280"+i,next);
        }
        for( int i = 10 ;i <= 99;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals("WS2020010128"+i,next);
        }
    }

    @Test
    public void testTimeHasNextExpire(){
        System.out.println("In testTimeHasNextExpire");
        currentTimeStub.setNow(2020,1,1);
        persistConfigRepositoryStub.set("class1",new PersistConfig("WS{year}{month}{day}{id:4}",10,"WS202001012801"));

        System.out.println("No Expired");
        for( int i = 1 ;i <= 5;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals("WS20200101280"+i,next);
        }

        currentTimeStub.setNow(2020,1,2);
        System.out.println("In Expired");
        for( int i = 1 ;i <= 9;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals("WS20200102000"+i,next);
        }
    }

    @Test
    public void testTimeInitExpire(){
        currentTimeStub.setNow(2020,1,1);
        persistConfigRepositoryStub.set("class1",new PersistConfig("WS{year}{month}{day}{id:4}",10,"WS201901012801"));

        for( int i = 1 ;i <= 9;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals("WS20200101000"+i,next);
        }

        for( int i = 10 ;i <= 99;i++){
            String next = persistGenerator.next(new Class1());
            assertEquals("WS2020010100"+i,next);
        }
    }

    @Test
    public void testTimeExpireTwoCounter(){
        currentTimeStub.setNow(2020,1,1);
        persistConfigRepositoryStub.set("class1",new PersistConfig("WS{year}{month}{day}{id:4}",10,"WS202001012801"));

        //使用两个生成器
        PersistGenerator generator1 = new PersistGenerator(persistCounterGenerator);
        PersistGenerator generator2 = new PersistGenerator(persistCounterGenerator);

        //counter1
        for( int i = 1 ;i <= 5;i++){
            String next = generator1.next(new Class1());
            assertEquals("WS20200101280"+i,next);
        }

        for( int i = 1 ;i <= 5;i++){
            String next = generator2.next(new Class1());
            assertEquals("WS20200101281"+i,next);
        }

        //时间突变，两个counter都没到尽头
        currentTimeStub.setNow(2020,1,2);
        for( int i = 1 ;i <= 5;i++){
            String next = generator2.next(new Class1());
            assertEquals("WS20200102000"+i,next);
        }

        for( int i = 1 ;i <= 5;i++){
            String next = generator1.next(new Class1());
            assertEquals("WS20200102001"+i,next);
        }
    }
}
