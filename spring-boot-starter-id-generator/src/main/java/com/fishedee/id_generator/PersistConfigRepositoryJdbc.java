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

    @Autowired
    private IdGeneratorConfigResolver idGeneratorConfigResolver;

    private String selectSql;

    private String updateSql;

    private String insertSql;

    private String selectAllSql;

    public PersistConfigRepositoryJdbc(String tableName,String beginCharacter,String endCharacter){
        String quoteTableName = beginCharacter+tableName+endCharacter;
        String quoteKeyName = beginCharacter+"key"+endCharacter;
        this.selectSql = String.format("select %s,template,step,initial_value,is_sync from %s where %s = ?",
                quoteKeyName,
                quoteTableName,
                quoteKeyName);
        this.updateSql = String.format("update %s set initial_value = ? where %s = ?",
                quoteTableName,
                quoteKeyName);
        this.insertSql = String.format("insert into %s(%s,template,step,initial_value,is_sync)values(?,?,?,?,?)",
                quoteTableName,
                quoteKeyName);
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

    private PersistConfig getAndAddDefaultConfig(String key,boolean insertWhenEmpty){
        PersistConfig config = idGeneratorConfigResolver.get(key);
        if( config == null ){
            throw new RuntimeException("没有"+key+"的主键生成器");
        }
        if( key.equals(config.getKey()) == false ){
            String msg = String.format("主键配置不匹配：%s != %s",
                    key,
                    config.getKey());
            throw new RuntimeException(msg);
        }
        if(insertWhenEmpty){
            this.add(key,config);
        }
        return new PersistConfig(config);
    }
    public PersistConfig get(String key){
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql,key);

        if( mapList.size() == 0 ){
            return this.getAndAddDefaultConfig(key,false);
        }
        return this.convertToConfig(mapList.get(0));
    }


    public PersistConfig getForUpdate(String key){
        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql+" for update",key);

        if( mapList.size() == 0 ){
            return this.getAndAddDefaultConfig(key,true);
        }
        return this.convertToConfig(mapList.get(0));
    }

    public void set(String key,PersistConfig config){
        jdbcTemplate.update(this.updateSql,config.getInitialValue(),key);
    }

    private void add(String key,PersistConfig config){
        jdbcTemplate.update(this.insertSql,
                key,
                config.getTemplate(),
                config.getStep(),
                config.getInitialValue(),
                config.getIsSync());
    }
}
