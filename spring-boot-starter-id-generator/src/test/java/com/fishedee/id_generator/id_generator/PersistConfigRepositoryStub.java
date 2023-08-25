package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.PersistConfig;
import com.fishedee.id_generator.PersistConfigRepository;
import com.fishedee.id_generator.TenantResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistConfigRepositoryStub implements PersistConfigRepository {
    private Map<String,Map<String,PersistConfig>> tenantConfig = new HashMap<>();

    private TenantResolver tenantResolver;

    public PersistConfigRepositoryStub(TenantResolver tenantResolver){
        this.tenantResolver = tenantResolver;
    }

    private Map<String,PersistConfig> getConfigMap(){
        String tenantId = this.tenantResolver.getTenantId();
        Map<String,PersistConfig> configMap = this.tenantConfig.get(tenantId);
        if( configMap == null ){
            configMap = new HashMap<>();
            this.tenantConfig.put(tenantId,configMap);
        }
        return configMap;
    }

    public PersistConfig get(String key) {
        return this.getConfigMap().get(key);
    }

    public PersistConfig getForUpdate(String key) {
        return this.getConfigMap().get(key);
    }

    public void set(String key,PersistConfig config) {
        PersistConfig oldConfig = this.getConfigMap().get(key);
        if( oldConfig == null ){
            this.getConfigMap().put(key,config);
        }else{
            oldConfig.setInitialValue(config.getInitialValue());
        }
    }

    public void clear(){
        this.getConfigMap().clear();
    }
}
