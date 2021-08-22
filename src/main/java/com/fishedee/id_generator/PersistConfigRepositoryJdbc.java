package com.fishedee.id_generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

public class PersistConfigRepositoryJdbc implements PersistConfigRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PersistConfig get(String key){
        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList("select * from id_generator_config where `key` = ? for update ",key);

        if( mapList.size() == 0 ){
            throw new RuntimeException("没有"+key+"的主键生成器");
        }
        Map<String,Object> single = mapList.get(0);
        PersistConfig result = new PersistConfig();
        result.setTemplate(single.get("template").toString());
        result.setStep(Integer.valueOf(single.get("step").toString()));
        result.setInitialValue(single.get("initial_value").toString());
        return result;
    }

    public void set(String key,PersistConfig config){
        jdbcTemplate.update("update id_generator_config set template = ?,step = ?,initial_value = ? where `key` = ?",
                config.getTemplate(),config.getStep(),config.getInitialValue(),key);
    }
}
