package com.fishedee.id_generator;

import com.fishedee.id_generator.place_template.PaddingParser;
import com.fishedee.id_generator.place_template.PaddingRender;
import com.fishedee.id_generator.place_template.Param;
import com.fishedee.id_generator.place_template.PlaceTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class NumberCounter {

    private Date dateTime;

    private NumberPersist config;

    private PlaceTemplate placeTemplate;

    private Param placeTemplateParam;

    private boolean hasInit;

    private Long currentId;

    public NumberCounter(Date dateTime, NumberPersist config){
        this.dateTime = dateTime;
        this.config = config;
        this.hasInit = false;
        this.currentId = 1L;
        this.initPlaceTemplate(config);
        this.initPlaceTemplateParam();
    }

    private void initPlaceTemplate(NumberPersist config){
        this.placeTemplate = new PlaceTemplate(config.getTemplate());
        //默认配置
        this.placeTemplate.addRender("year",new PaddingRender(false,4));
        this.placeTemplate.addParser("year",new PaddingParser(false,4));
        this.placeTemplate.addRender("month",new PaddingRender(false,2));
        this.placeTemplate.addParser("month",new PaddingParser(false,2));
        this.placeTemplate.addRender("day",new PaddingRender(false,2));
        this.placeTemplate.addParser("day",new PaddingParser(false,2));
        this.placeTemplateParam = this.placeTemplate.extractParam("");
    }

    private void initPlaceTemplateParam(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.dateTime);

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

    public boolean hasInitId(){
        return hasInit;
    }

    public void initId(Optional<Long> maxId){
        if( hasInit == true ){
            throw new IdGeneratorException(1,"重复初始化编号生成器",null);
        }

        //设置标记
        if( maxId.isPresent()){
            this.currentId = maxId.get().longValue()+1;
        }else{
            this.currentId = 1L;
        }
        this.hasInit = true;
    }

    public String peek(){
        this.placeTemplateParam.put("id",this.currentId);
        return this.placeTemplate.format(this.placeTemplateParam);
    }

    public void next(){
        this.currentId++;
    }

    public String getMatchIdRegex(){
        this.placeTemplateParam.put("id",this.currentId);
        return this.placeTemplate.calcMatchIdRegex(this.placeTemplateParam);
    }

    public NumberPersist getConfig(){
        return this.config;
    }

}
