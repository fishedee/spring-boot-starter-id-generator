package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.CurrentTime;

import java.util.Calendar;
import java.util.Date;

public class CurrentTimeStub implements CurrentTime {
    private Date now;

    public void setNow(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        this.now = calendar.getTime();
    }

    @Override
    public Date now() {
        return this.now;
    }

    @Override
    public String toString(){
        return "CurrentTimeStub "+now.toString();
    }
}
