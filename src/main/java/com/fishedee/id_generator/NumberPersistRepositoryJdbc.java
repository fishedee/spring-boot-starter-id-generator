package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class NumberPersistRepositoryJdbc implements NumberPersistRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NumberGeneratorConfigResolver numberGeneratorConfigResolver;

    private String selectSql;

    private String insertSql;

    public NumberPersistRepositoryJdbc(String tableName,String beginCharacter,String endCharacter){
        String quoteTableName = beginCharacter+tableName+endCharacter;
        String quoteKeyName = beginCharacter+"key"+endCharacter;
        this.selectSql = String.format("select %s,template from %s where %s = ?",
                quoteKeyName,
                quoteTableName,
                quoteKeyName);
        this.insertSql = String.format("insert into %s(%s,template)values(?,?)",
                quoteTableName,
                quoteKeyName);
    }

    private NumberPersist convertToConfig(Map<String,Object> single){
        NumberPersist result = new NumberPersist();
        result.setKey(single.get("key").toString());
        result.setTemplate(single.get("template").toString());
        return result;
    }

    private NumberPersist getAndAddDefaultConfig(String key){
        NumberPersist config = numberGeneratorConfigResolver.get(key);
        if( config == null ){
            throw new RuntimeException("没有"+key+"的编号生成器");
        }
        if( key.equals(config.getKey()) == false ){
            String msg = String.format("主键配置不匹配：%s != %s",
                    key,
                    config.getKey());
            throw new RuntimeException(msg);
        }
        this.add(key,config);
        return new NumberPersist(config);
    }

    @Override
    public NumberPersist get(String key){

        //for update锁
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(this.selectSql,key);

        if( mapList.size() == 0 ){
            return getAndAddDefaultConfig(key);
        }
        return this.convertToConfig(mapList.get(0));
    }

    private void add(String key,NumberPersist config){
        jdbcTemplate.update(this.insertSql,
                key,
                config.getTemplate());
    }
}
