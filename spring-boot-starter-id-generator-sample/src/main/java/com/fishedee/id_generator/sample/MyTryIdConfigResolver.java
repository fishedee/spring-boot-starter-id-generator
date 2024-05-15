package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.TryIdGeneratorConfigResolver;
import com.fishedee.id_generator.TryPersist;
import org.springframework.stereotype.Component;

@Component
public  class MyTryIdConfigResolver implements TryIdGeneratorConfigResolver {
    @Override
    public TryPersist get(String key){
        if( key.equals("try_st")){
            return new TryPersist()
                    .setKey("try_st")
                    .setTemplate("SST{year}{id:2+}")
                    .setInitialValue("");
        }
        return null;
    }
}