package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;

public class ParserFactory {
    public static Parser getParser(String argument){
        argument.trim();
        if( argument.length() == 0 ){
            return new DefaultParser();
        }else{
            try{
                int argumentValue = Integer.valueOf(argument);
                return new PaddingParser(argumentValue);
            }catch(NumberFormatException e){
                throw new IdGeneratorException(1,"模板参数错误["+argument+"]",null);
            }
        }
    }
}
