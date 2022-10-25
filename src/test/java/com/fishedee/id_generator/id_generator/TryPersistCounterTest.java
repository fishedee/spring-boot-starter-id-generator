package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MyConfig.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TryPersistCounterTest {

    @Autowired
    private CurrentTimeStub currentTimeStub;

    @Autowired
    private PersistConfigRepositoryStub persistConfigRepositoryStub;

    @Test
    public void testIncrementOne(){
        currentTimeStub.setNow(2000,1,1);
        //只步进1个
        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","{id}","10"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"{id}\",initialValue:\"10\",key:null}",
                counter.getNextTry()
        );

        for( int i = 10 ;i <= 20;i++){
            assertEquals(counter.peek(),i+"");
            counter.next();
        }

        JsonAssertUtil.checkEqualStrict(
                "{template:\"{id}\",initialValue:\"21\",key:null}",
                counter.getNextTry()
        );
    }


    @Test
    public void testIncrementOneWithTime(){
        currentTimeStub.setNow(2000,1,1);

        TryPersistCounter counter = new TryPersistCounter(currentTimeStub,new TryPersist("testKey","XS{year}{month}{day}{id:4}","XS200001019901"));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001019901\",key:null}",
                counter.getNextTry()
        );

        for( int i = 1 ;i <= 20;i++){
            assertEquals(counter.peek(),String.format("XS2000010199%02d",i));
            counter.next();
        }

        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",initialValue:\"XS200001019921\",key:null}",
                counter.getNextTry()
        );
    }

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
}
