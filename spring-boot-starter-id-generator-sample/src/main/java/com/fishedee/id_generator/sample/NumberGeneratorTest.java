package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.NumberCounter;
import com.fishedee.id_generator.NumberGenerator;
import com.fishedee.id_generator.TryPersistGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Slf4j
public class NumberGeneratorTest {

    @Autowired
    private NumberGenerator numberGenerator;

    public Date getDate(int year, int month, int day){

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,day);

        return calendar.getTime();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test(String key){
        Date date1 = getDate(2023,01,01);
        Date date2 = getDate(2023,01,02);
        Date date3 = getDate(2023,03,01);
        Date date4 = getDate(2024,01,01);

        List<Date> dateList = Arrays.asList(
                date1,
                date2,
                date3,
                date4,
                date1
        );
        for( Date date : dateList ){
            NumberCounter numberCounter = numberGenerator.get(date,key);
            if( numberCounter.hasInitId() == false ){
                numberCounter.initId(Optional.empty());
            }
            for( int i = 0 ; i != 5;i++){
                String key1 = numberCounter.peek();
                log.info("key:{}, number :{}",key,key1);
                numberCounter.next();
            }
        }
    }

    public void run(){
        NumberGeneratorTest app = (NumberGeneratorTest) AopContext.currentProxy();
        log.info("--- test number_id ---");
        app.test("number_id");

        log.info("--- test number_order_id ---");
        app.test("number_order_id");

        log.info("--- test number_order_id2 ---");
        app.test("number_order_id2");

        log.info("--- test number_order_id3 ---");
        app.test("number_order_id3");
    }
}
