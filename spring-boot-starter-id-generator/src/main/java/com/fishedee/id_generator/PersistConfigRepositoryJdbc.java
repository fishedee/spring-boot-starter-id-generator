package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
public class PersistConfigRepositoryJdbc implements PersistConfigRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String selectSql;

    private String updateSql;

    public PersistConfigRepositoryJdbc(String tableName){
        this.selectSql = String.format("select template,step,initial_value from `%s` where `key` = ? for update ",tableName);
        this.updateSql = String.format("update `%s` set template = ?,step = ?,initial_value = ? where `key` = ?",tableName);
        log.info("{}",this.selectSql);
    }

    public PersistConfig get(String key){
        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql,key);

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
        jdbcTemplate.update(this.updateSql,
                config.getTemplate(),config.getStep(),config.getInitialValue(),key);
    }
}
