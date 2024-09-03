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
    public void testIncrementOneWithTime(){

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

    /*
    @Test
    public void testTimeAtFirstExpire(){
        currentTimeStub.setNow(2000,1,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}{id:4}","XS200001020010"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001020010\",key:null}",
                counter.getNextTry()
        );

        currentTimeStub.setNow(2000,1,3);

        for( int i = 1 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2000010300%02d",i));
            counter.next();
        }
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001030021\",key:null}",
                counter.getNextTry()
        );
    }

    @Test
    public void testTimePeekExpire(){
        currentTimeStub.setNow(2000,1,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}{id:4}","XS200001020010"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001020010\",key:null}",
                counter.getNextTry()
        );


        for( int i = 10 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2000010200%02d",i));
            counter.next();
        }

        currentTimeStub.setNow(2000,1,3);

        for( int i = 1 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2000010300%02d",i));
            counter.next();
        }
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001030021\",key:null}",
                counter.getNextTry()
        );
    }

    @Test
    public void testTimeNextExpire(){
        currentTimeStub.setNow(2000,1,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}{id:4}","XS200001020010"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001020010\",key:null}",
                counter.getNextTry()
        );

        for( int i = 10 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2000010200%02d",i));
            counter.next();
        }

        currentTimeStub.setNow(2000,1,3);
        counter.next();

        for( int i = 1 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2000010300%02d",i));
            counter.next();
        }
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001030021\",key:null}",
                counter.getNextTry()
        );
    }

    @Test
    public void testTimeOnlyYearExpire() {
        currentTimeStub.setNow(1999,1,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{id:4}","XS19980011"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{id:4}\",initialValue:\"XS19990001\",key:null}",
                counter.getNextTry()
        );
    }

    @Test
    public void testTimeOnlyYearAndMonthExpire() {
        currentTimeStub.setNow(1999,2,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{id:4}","XS1999010011"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{id:4}\",initialValue:\"XS1999020001\",key:null}",
                counter.getNextTry()
        );
    }

    @Test
    public void testNotValid(){
        currentTimeStub.setNow(1999,2,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}{id:4}","XS1999"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS199902020001\",key:null}",
                counter.getNextTry()
        );

        for( int i = 1 ;i != 10;i++){
            assertEquals(counter.peek(),"XS19990202000"+i);
            counter.next();
        }
    }

    @Test
    public void testLackOfId(){
        currentTimeStub.setNow(1999,2,2);
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}","XS19990202"));
        });
        assertTrue(e.getMessage().contains("缺乏id参数"));
    }

    @Test
    public void testInitialValueEmpty(){
        currentTimeStub.setNow(1999,2,2);
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}{id}",""));
        for( int i = 1 ;i != 20;i++){
            assertEquals(counter.peek(),"XS19990202"+i);
            counter.next();
        }
    }

     */
}
