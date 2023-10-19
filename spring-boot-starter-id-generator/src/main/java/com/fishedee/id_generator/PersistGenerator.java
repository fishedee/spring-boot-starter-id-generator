package com.fishedee.id_generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.omg.CORBA.Current;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.css.Counter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class PersistGenerator extends AbstractGenerator{

    private Map<String,PersistCounter> counterMap;

    private PersistCounterGenerator persistCounterGenerator;

    private PersistConfigRepository persistConfigRepository;

    private Map<String,Boolean> syncCounterConfig;

    private TenantResolver tenantResolver;

    public PersistGenerator(PersistCounterGenerator persistCounterGenerator,
                            PersistConfigRepository persistConfigRepository,
                            TenantResolver tenantResolver){
        counterMap = new HashMap<>();
        syncCounterConfig = new HashMap<>();
        this.persistCounterGenerator = persistCounterGenerator;
        this.persistConfigRepository = persistConfigRepository;
        this.tenantResolver = tenantResolver;
    }

    @Override
    public void clearCache(boolean isClearAllTenant){
        synchronized (this){
            if( isClearAllTenant ){
                //所有租户数据都清除
                counterMap.clear();
                syncCounterConfig.clear();
            }else{
                //只清除当前租户，先找出所有的key
                String tenantIdPrefix = this.tenantResolver.getTenantId()+"###";
                List<String> targetCounterKeys = counterMap.keySet().stream().filter(single->{
                    return single.startsWith(tenantIdPrefix);
                }).collect(Collectors.toList());
                List<String> targetSyncKeys = syncCounterConfig.keySet().stream().filter(single->{
                    return single.startsWith(tenantIdPrefix);
                }).collect(Collectors.toList());
                //批量删除key
                targetCounterKeys.forEach(single->{
                    counterMap.remove(single);
                });
                targetSyncKeys.forEach(single->{
                    syncCounterConfig.remove(single);
                });
            }
        }
    }

    @Override
    //同步化代码，只有一个线程能进入这里
    public  String next(String dataKey){
        synchronized(this) {
            //key
            String tenantId = this.tenantResolver.getTenantId();
            String key = tenantId + "###" + dataKey;

            //获取同步配置
            Boolean isSync = syncCounterConfig.get(key);
            if (isSync == null) {
                //这里不用for update获取，是因为外部可能有事务，直接用for update会可能导致死锁
                PersistConfig config = persistConfigRepository.get(dataKey);
                isSync = (config.getIsSync() == 1);
                syncCounterConfig.put(key, isSync);
            }

            //获取counter
            PersistCounter counter;
            if (isSync == true) {
                counter = persistCounterGenerator.getCounterSync(dataKey);
                counter.setTenantId(tenantId);
            } else {
                counter = counterMap.get(key);
                if (counter == null || counter.hasNext() == false) {
                    //本地没有counter，或者counter已经到了尽头
                    counter = persistCounterGenerator.getCounterAsync(dataKey);
                    counter.setTenantId(tenantId);
                    counterMap.put(key, counter);
                }
            }

            //禁止更新sync配置，这样可能会导致死锁，同一个key在一个事务中，既执行sync，也执行async就会死锁
            //syncCounterConfig.put(key,counter.getIsSync());

            //获取counter的下一个值
            return counter.next();
        }
    }
}
