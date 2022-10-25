package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;

public class ParserFactory {
    public static Parser getParser(String argument){
        argument.trim();
        if( argument.length() == 0 ){
            return new DefaultParser();
        }else{
            try{
                boolean allowOverPadding = false;
                int argumentValue = 0;
                if( argument.charAt(argument.length()-1) == '+'){
                    allowOverPadding = true;
                    argumentValue = Integer.valueOf(argument.substring(0,argument.length()-1).trim());
                }else{
                    allowOverPadding = false;
                    argumentValue = Integer.valueOf(argument);
                }
                return new PaddingParser(allowOverPadding,argumentValue);
            }catch(NumberFormatException e){
                throw new IdGeneratorException(1,"模板参数错误["+argument+"]",null);
            }
        }
    }
}
