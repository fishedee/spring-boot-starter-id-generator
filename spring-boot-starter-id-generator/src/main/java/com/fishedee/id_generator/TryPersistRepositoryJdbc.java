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

    @Autowired
    private TryIdGeneratorConfigResolver tryIdGeneratorConfigResolver;

    private String selectSql;

    private String updateSql;

    private String insertSql;

    public TryPersistRepositoryJdbc(String tableName,String beginCharacter,String endCharacter){
        String quoteTableName = beginCharacter+tableName+endCharacter;
        String quoteKeyName = beginCharacter+"key"+endCharacter;
        this.selectSql = String.format("select %s,template,initial_value from %s where %s = ?",
                quoteKeyName,
                quoteTableName,
                quoteKeyName);
        this.updateSql = String.format("update %s set initial_value = ? where %s = ?",
                quoteTableName,
                quoteKeyName);
        this.insertSql = String.format("insert into %s(%s,template,initial_value)values(?,?,?)",
                quoteTableName,
                quoteKeyName);
    }

    private TryPersist convertToConfig(Map<String,Object> single){
        TryPersist result = new TryPersist();
        result.setKey(single.get("key").toString());
        result.setTemplate(single.get("template").toString());
        result.setInitialValue(single.get("initial_value").toString());
        return result;
    }

    private TryPersist getAndAddDefaultConfig(String key){
        TryPersist config = tryIdGeneratorConfigResolver.get(key);
        if( config == null ){
            throw new RuntimeException("没有"+key+"的主键生成器");
        }
        if( key.equals(config.getKey()) == false ){
            String msg = String.format("主键配置不匹配：%s != %s",
                    key,
                    config.getKey());
            throw new RuntimeException(msg);
        }
        this.add(key,config);
        return new TryPersist(config);
    }

    public TryPersist getForUpdate(String key){

        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql+" for update",key);

        if( mapList.size() == 0 ){
            return getAndAddDefaultConfig(key);
        }
        return this.convertToConfig(mapList.get(0));
    }

    public void set(String key,TryPersist config){
        jdbcTemplate.update(this.updateSql,config.getInitialValue(),key);
    }

    private void add(String key,TryPersist config){
        jdbcTemplate.update(this.insertSql,
                key,
                config.getTemplate(),
                config.getInitialValue());
    }
}
