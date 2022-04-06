package com.fishedee.id_generator;

import com.fishedee.id_generator.place_template.PaddingParser;
import com.fishedee.id_generator.place_template.PaddingRender;
import com.fishedee.id_generator.place_template.Param;
import com.fishedee.id_generator.place_template.PlaceTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public class PersistCounter {
    private CurrentTime currentTime;

    private PlaceTemplate placeTemplate;

    private Param placeTemplateParam;

    private int step;

    private Long initId;

    private Long currentId;

    private boolean isSync;

    public PersistCounter(CurrentTime currentTime,PersistConfig config){
        this.currentTime = currentTime;
        this.initPlaceTemplate(config);
        this.initStep(config.getStep());
        Date now = this.currentTime.now();
        this.resetTimeIfExpire(now);
        this.initId();
        this.initSync(config);
    }

    private void initSync(PersistConfig config){
        if( config.getIsSync().byteValue() == 1 ){
            if( config.getStep() != 1){
                throw new RuntimeException("IdGeneratorConfig["+config.getKey()+"]设置为同步的时候，step必须为1");
            }
            this.isSync = true;
        }else if( config.getIsSync().byteValue() == 0 ){
            this.isSync = false;
        }else{
            throw new RuntimeException("IdGeneratorConfig["+config.getKey()+"]的不合法isSync["+config.getIsSync()+"]");
        }
    }

    public boolean getIsSync(){
        return this.isSync;
    }

    private void initPlaceTemplate(PersistConfig config){
        this.placeTemplate = new PlaceTemplate(config.getTemplate());
        //默认配置
        this.placeTemplate.addRender("year",new PaddingRender(4));
        this.placeTemplate.addParser("year",new PaddingParser(4));
        this.placeTemplate.addRender("month",new PaddingRender(2));
        this.placeTemplate.addParser("month",new PaddingParser(2));
        this.placeTemplate.addRender("day",new PaddingRender(2));
        this.placeTemplate.addParser("day",new PaddingParser(2));
        this.placeTemplateParam = this.placeTemplate.extractParam(config.getInitialValue());
    }

    private void initStep(int step){
        if( step <= 0 ){
            throw new IdGeneratorException(1,"步长不能为负数或者0:"+step,null);
        }
        this.step = step;
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

    public boolean hasNext(){
        Date now = currentTime.now();
        if( isTimeExpire(now)){
            return false;
        }

        if( this.currentId >= this.initId + this.step){
            return false;
        }
        return true;
    }

    public String next(){
        this.placeTemplateParam.put("id",this.currentId);
        this.currentId++;
        return this.placeTemplate.format(this.placeTemplateParam);
    }

    public PersistConfig getNextConfig(){
        this.placeTemplateParam.put("id",this.initId+this.step);
        String nextInitalValue = this.placeTemplate.format(this.placeTemplateParam);

        PersistConfig config = new PersistConfig();
        config.setTemplate(this.placeTemplate.getTemplate());
        config.setStep(this.step);
        config.setInitialValue(nextInitalValue);
        return config;
    }
}
