package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PersistConfigRepositoryJdbc implements PersistConfigRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String selectSql;

    private String updateSql;

    private String selectAllSql;

    public PersistConfigRepositoryJdbc(String tableName,String beginCharacter,String endCharacter){
        this.selectSql = "select "+beginCharacter+"key"+endCharacter+",template,step,initial_value,is_sync from "+beginCharacter+tableName+endCharacter+ " where "+beginCharacter+"key"+endCharacter+" = ?";
        this.updateSql = "update "+beginCharacter+tableName+endCharacter+" set initial_value = ? where "+beginCharacter+"key"+endCharacter+" = ?";
    }

    private PersistConfig convertToConfig(Map<String,Object> single){
        PersistConfig result = new PersistConfig();
        result.setKey(single.get("key").toString());
        result.setTemplate(single.get("template").toString());
        result.setStep(Integer.valueOf(single.get("step").toString()));
        result.setInitialValue(single.get("initial_value").toString());
        result.setIsSync(Byte.valueOf(single.get("is_sync").toString()));
        return result;
    }

    public PersistConfig get(String key){
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql,key);

        if( mapList.size() == 0 ){
            throw new RuntimeException("没有"+key+"的主键生成器");
        }
        return this.convertToConfig(mapList.get(0));
    }


    public PersistConfig getForUpdate(String key){

        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql+" for update",key);

        if( mapList.size() == 0 ){
            throw new RuntimeException("没有"+key+"的主键生成器");
        }
        return this.convertToConfig(mapList.get(0));
    }

    public void set(String key,PersistConfig config){
        jdbcTemplate.update(this.updateSql,config.getInitialValue(),key);
    }
}
