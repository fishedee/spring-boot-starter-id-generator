package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MyConfig.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NumberPersistCounterTest {

    @Autowired
    private NumberPersistRepositoryStub numberPersistRepositoryStub;

    public Date getDate(int year, int month, int day){

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,day);

        return calendar.getTime();
    }

    @Test
    public void testIncrementOneWithMaxId(){
        //只步进1个
        NumberCounter counter = new NumberCounter(getDate(2023,12,10),
                new NumberPersist("testKey","{id}"));
        assertEquals(counter.hasInitId(),false);
        counter.initId(Optional.of(9L));
        assertEquals(counter.hasInitId(),true);
        assertEquals(
                "10",
                counter.peek()
        );

        for( int i = 10 ;i <= 20;i++){
            assertEquals(counter.peek(),i+"");
            assertEquals(counter.getMatchIdRegex(),"(\\d+)");
            counter.next();
        }
    }

    @Test
    public void testIncrementOneWithNotMaxId(){
        //只步进1个
        NumberCounter counter = new NumberCounter(getDate(2023,12,10),
                new NumberPersist("testKey","{id}"));
        counter.initId(Optional.empty());
        assertEquals(
                "1",
                counter.peek()
        );

        for( int i = 10 ;i <= 20;i++){
            assertEquals(counter.peek(),(i-9)+"");
            assertEquals(counter.getMatchIdRegex(),"(\\d+)");
            counter.next();
        }
    }


    @Test
    public void testIncrementTimeWithNotMaxId(){

        NumberCounter counter = new NumberCounter(getDate(2012,03,04),
                new NumberPersist("testKey","XS{year}{month}{day}{id:4}"));
        counter.initId(Optional.empty());
        assertEquals(
                "XS201203040001",
                counter.peek()
        );

        for( int i = 1 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2012030400%02d",i));
            assertEquals(counter.getMatchIdRegex(),"XS20120304(\\d+)");
            counter.next();
        }
    }

    @Test
    public void testIncrementTimeWithMaxId(){

        NumberCounter counter = new NumberCounter(getDate(2012,03,04),
                new NumberPersist("testKey","XS{year}{month}{day}{id:4}"));
        counter.initId(Optional.of(100L));
        assertEquals(
                "XS201203040101",
                counter.peek()
        );

        for( int i = 1 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2012030401%02d",i));
            assertEquals(counter.getMatchIdRegex(),"XS20120304(\\d+)");
            counter.next();
        }
    }

    @Test
    public void testLackOfId(){

        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            NumberCounter counter = new NumberCounter(getDate(2012,03,04),
                    new NumberPersist("testKey","XS{year}{month}{day}"));
        });
        assertTrue(e.getMessage().contains("缺乏id参数"));
    }

    @Test
    public void testInitDuplicate(){
        NumberCounter counter = new NumberCounter(getDate(2012,03,04),
                new NumberPersist("testKey","XS{year}{month}{day}{id:4+}"));

        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            counter.initId(Optional.empty());
            counter.initId(Optional.empty());
        });
        assertTrue(e.getMessage().contains("重复初始化编号生成器"));
    }
}
