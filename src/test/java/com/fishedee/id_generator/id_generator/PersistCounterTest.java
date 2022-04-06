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
public class PersistCounterTest {

    @Autowired
    private CurrentTimeStub currentTimeStub;

    @Autowired
    private PersistConfigRepositoryStub persistConfigRepositoryStub;

    @Test
    public void testIncrementOne(){
        currentTimeStub.setNow(2000,1,1);
        //只步进1个
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","{id}",1,"10",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"{id}\",step:1,initialValue:\"11\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"10");

        assertFalse(counter.hasNext());
    }

    @Test
    public void testIncrementTwo(){
        currentTimeStub.setNow(2000,1,1);
        //只步进2个
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","{id}",2,"101",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"{id}\",step:2,initialValue:\"103\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"101");

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"102");

        assertFalse(counter.hasNext());

    }

    @Test
    public void testIncrementZero(){
        currentTimeStub.setNow(2000,1,1);
        //只步进0个
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","{id}",0,"101",(byte)0));
        });
        assertTrue(e.getMessage().contains("步长不能为负数或者0"));
    }

    @Test
    public void testIncrementNegative(){
        currentTimeStub.setNow(2000,1,1);
        //只步进0个
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","{id}",-2,"101",(byte)0));
        });
        assertTrue(e.getMessage().contains("步长不能为负数或者0"));
    }

    @Test
    public void testIncrementOneWithTime(){
        currentTimeStub.setNow(2000,1,1);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id}",1,"XS2000010110",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id}\",step:1,initialValue:\"XS2000010111\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"XS2000010110");

        assertFalse(counter.hasNext());

    }

    @Test
    public void testIncrementTenWithTime(){
        currentTimeStub.setNow(2000,1,1);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id}",10,"XS2000010110",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id}\",step:10,initialValue:\"XS2000010120\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        for( int i = 10 ;i != 20;i++){
            assertTrue(counter.hasNext());
            assertEquals(counter.next(),"XS20000101"+i);
        }

        assertFalse(counter.hasNext());

    }

    @Test
    public void testTimeExpire(){
        currentTimeStub.setNow(2000,1,2);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id:4}",10,"XS200001010010",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",step:10,initialValue:\"XS200001020011\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        for( int i = 1 ;i != 10;i++){
            assertTrue(counter.hasNext());
            assertEquals(counter.next(),"XS20000102000"+i);
        }

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"XS200001020010");

        assertFalse(counter.hasNext());
    }

    @Test
    public void testTimeHasNextExpire(){
        currentTimeStub.setNow(1999,1,2);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id:4}",9,"XS199901020010",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",step:9,initialValue:\"XS199901020019\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"XS199901020010");

        assertTrue(counter.hasNext());
        assertEquals(counter.next(),"XS199901020011");

        //走到一半时间变了
        currentTimeStub.setNow(1999,1,3);
        assertFalse(counter.hasNext());

        PersistCounter counter2 = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id:4}",9,"XS199901020019",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",step:9,initialValue:\"XS199901030010\",isSync:null,key:null}",
                counter2.getNextConfig()
        );
    }

    @Test
    public void testTimeOnlyYearExpire() {
        currentTimeStub.setNow(1999,1,2);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{id:4}",9,"XS19980011",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{id:4}\",step:9,initialValue:\"XS19990010\",isSync:null,key:null}",
                counter.getNextConfig()
        );
    }

    @Test
    public void testTimeOnlyYearAndMonthExpire() {
        currentTimeStub.setNow(1999,2,2);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{id:4}",9,"XS1999010011",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{id:4}\",step:9,initialValue:\"XS1999020010\",isSync:null,key:null}",
                counter.getNextConfig()
        );
    }

    @Test
    public void testNotValid(){
        currentTimeStub.setNow(1999,2,2);
        PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id:4}",9,"XS1999",(byte)0));
        JsonAssertUtil.checkEqualStrict(
                "{template:\"XS{year}{month}{day}{id:4}\",step:9,initialValue:\"XS199902020010\",isSync:null,key:null}",
                counter.getNextConfig()
        );

        for( int i = 1 ;i != 10;i++){
            assertTrue(counter.hasNext());
            assertEquals(counter.next(),"XS19990202000"+i);
        }
        assertFalse(counter.hasNext());
    }

    @Test
    public void testLackOfId(){
        currentTimeStub.setNow(1999,2,2);
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}",9,"XS19990202",(byte)0));
        });
        assertTrue(e.getMessage().contains("缺乏id参数"));
    }

    @Test
    public void testStepNagative(){
        currentTimeStub.setNow(1999,2,2);
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PersistCounter counter = new PersistCounter(currentTimeStub,new PersistConfig("testKey","XS{year}{month}{day}{id}",0,"XS199902021",(byte)0));
        });
        assertTrue(e.getMessage().contains("步长不能为负数或者0"));
    }
}
