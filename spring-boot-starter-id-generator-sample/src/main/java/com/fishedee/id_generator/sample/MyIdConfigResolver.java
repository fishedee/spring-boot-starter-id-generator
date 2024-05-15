package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGeneratorConfigResolver;
import com.fishedee.id_generator.PersistConfig;
import org.springframework.stereotype.Component;

@Component
public  class MyIdConfigResolver implements IdGeneratorConfigResolver {
    @Override
    public PersistConfig get(String key){
        if( key.equals("order.stock_order")){
            return new PersistConfig()
                    .setKey("order.stock_order")
                    .setTemplate("ST{year}{month}{day}{id:8}")
                    .setInitialValue("")
                    .setStep(10)
                    .setIsSync((byte)0);
        }
        return null;
    }
}