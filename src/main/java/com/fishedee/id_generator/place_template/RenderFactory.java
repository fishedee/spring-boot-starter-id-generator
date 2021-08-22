package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;

public class RenderFactory {
    public static Render getRender(String argument){
        argument.trim();
        if( argument.length() == 0 ){
            return new DefaultRender();
        }else{
            try{
                int argumentValue = Integer.valueOf(argument);
                return new PaddingRender(argumentValue);
            }catch(NumberFormatException e){
                throw new IdGeneratorException(1,"模板参数错误["+argument+"]",null);
            }
        }
    }
}
