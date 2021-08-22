package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;

public class PaddingParser implements Parser{
    private int padding;
    public PaddingParser(int padding){
        if (padding <= 0) {
            throw new IdGeneratorException(1, "补齐不能为负数[" + padding + "]", null);
        }
        this.padding = padding;
    }

    @Override
    public Parser.Result parse(String input,int index){
        int endIndex = index+padding;
        if( endIndex > input.length() ){
            //长度不足
            return new Parser.Result(0,0L,false);
        }
        String argumentValue = input.substring(index,endIndex).trim();
        try{
            Long argument = Long.valueOf(argumentValue);
            return new Parser.Result(endIndex,argument,true);
        }catch(NumberFormatException e){
            return new Parser.Result(0,0L,false);
        }
    }
}
