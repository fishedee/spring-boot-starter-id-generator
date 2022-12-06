package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class TryPersistRepositoryJdbc implements TryPersistRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String selectSql;

    private String updateSql;

    public TryPersistRepositoryJdbc(String tableName,String beginCharacter,String endCharacter){
        this.selectSql = "select "+beginCharacter+"key"+endCharacter+",template,initial_value from "+beginCharacter+tableName+endCharacter+ " where "+beginCharacter+"key"+endCharacter+" = ?";
        this.updateSql = "update "+beginCharacter+tableName+endCharacter+" set initial_value = ? where "+beginCharacter+"key"+endCharacter+" = ?";
    }

    private TryPersist convertToConfig(Map<String,Object> single){
        TryPersist result = new TryPersist();
        result.setKey(single.get("key").toString());
        result.setTemplate(single.get("template").toString());
        result.setInitialValue(single.get("initial_value").toString());
        return result;
    }

    public TryPersist getForUpdate(String key){

        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql+" for update",key);

        if( mapList.size() == 0 ){
            throw new RuntimeException("没有"+key+"的主键生成器");
        }
        return this.convertToConfig(mapList.get(0));
    }

    public void set(String key,TryPersist config){
        jdbcTemplate.update(this.updateSql,config.getInitialValue(),key);
    }
}
