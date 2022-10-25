package com.fishedee.id_generator;

import com.fishedee.id_generator.place_template.PaddingParser;
import com.fishedee.id_generator.place_template.PaddingRender;
import com.fishedee.id_generator.place_template.Param;
import com.fishedee.id_generator.place_template.PlaceTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public class TryPersistCounter {
    private CurrentTime currentTime;

    private PlaceTemplate placeTemplate;

    private Param placeTemplateParam;

    private Long initId;

    private Long currentId;

    public TryPersistCounter(CurrentTime currentTime,TryPersist config){
        this.currentTime = currentTime;
        this.initPlaceTemplate(config);
        Date now = this.currentTime.now();
        this.resetTimeIfExpire(now);
        this.initId();
    }

    private void initPlaceTemplate(TryPersist config){
        this.placeTemplate = new PlaceTemplate(config.getTemplate());
        //默认配置
        this.placeTemplate.addRender("year",new PaddingRender(false,4));
        this.placeTemplate.addParser("year",new PaddingParser(false,4));
        this.placeTemplate.addRender("month",new PaddingRender(false,2));
        this.placeTemplate.addParser("month",new PaddingParser(false,2));
        this.placeTemplate.addRender("day",new PaddingRender(false,2));
        this.placeTemplate.addParser("day",new PaddingParser(false,2));
        this.placeTemplateParam = this.placeTemplate.extractParam(config.getInitialValue());
    }

    private boolean isTimeExpire(Date now){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        //需要校对年份
        if( this.placeTemplateParam.containsKey("year") ){
            Long year = this.placeTemplateParam.get("year");
            if( year == null ){
                return true;
            }
            if( year.intValue() != calendar.get(Calendar.YEAR) ){
                return true;
            }
        }

        //需要校对月份
        if( this.placeTemplateParam.containsKey("month") ){
            Long month = this.placeTemplateParam.get("month");
            if( month == null ){
                return true;
            }
            if( month.intValue() != calendar.get(Calendar.MONTH)+1 ){
                return true;
            }
        }

        //需要校对天
        if( this.placeTemplateParam.containsKey("day") ){
            Long day = this.placeTemplateParam.get("day");
            if( day == null ){
                return true;
            }
            if( day.intValue() != calendar.get(Calendar.DAY_OF_MONTH) ){
                return true;
            }
        }

        return false;
    }

    private void resetTimeIfExpire(Date now){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        boolean isExpire = isTimeExpire(now);
        if( isExpire == false ){
            return;
        }


        //重置year
        if( this.placeTemplateParam.containsKey("year") ) {
            this.placeTemplateParam.put("year",new Long(calendar.get(Calendar.YEAR)));
        }

        //重置month
        if( this.placeTemplateParam.containsKey("month") ) {
            this.placeTemplateParam.put("month",new Long(calendar.get(Calendar.MONTH)+1));
        }

        //重置day
        if( this.placeTemplateParam.containsKey("day") ) {
            this.placeTemplateParam.put("day",new Long(calendar.get(Calendar.DAY_OF_MONTH)));
        }

        //重置id
        if( this.placeTemplateParam.containsKey("id") == false ){
            throw new IdGeneratorException(1,"模板里面缺乏id参数",null);
        }
        this.placeTemplateParam.put("id",1L);
    }

    private void initId(){
        if( this.placeTemplateParam.containsKey("id") == false ){
            throw new IdGeneratorException(1,"模板里面缺乏id参数",null);
        }
        Long initId = this.placeTemplateParam.get("id");
        if( initId == null){
            throw new IdGeneratorException(1,"模板里面id参数为null",null);
        }
        this.initId = initId;
        this.currentId = this.initId;
    }

    public String peek(){
        Date now = this.currentTime.now();
        if( isTimeExpire(now)){
            this.next();
        }
        this.placeTemplateParam.put("id",this.currentId);
        return this.placeTemplate.format(this.placeTemplateParam);
    }

    public void next(){
        Date now = this.currentTime.now();
        if( isTimeExpire(now)){
            this.resetTimeIfExpire(now);
            this.initId();
        }else{
            this.currentId++;
        }
    }

    public TryPersist getNextTry(){
        TryPersist result = new TryPersist();
        result.setInitialValue(peek());
        result.setTemplate(this.placeTemplate.getTemplate());
        return result;
    }
}
