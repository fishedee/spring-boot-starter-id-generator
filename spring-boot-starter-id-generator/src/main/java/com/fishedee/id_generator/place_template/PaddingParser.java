package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;

public class PaddingParser implements Parser{
    private boolean allowOverPadding;
    private int padding;
    public PaddingParser(boolean allowOverPadding,int padding){
        if (padding <= 0) {
            throw new IdGeneratorException(1, "补齐不能为负数[" + padding + "]", null);
        }
        this.allowOverPadding = allowOverPadding;
        this.padding = padding;
    }

    @Override
    public Parser.Result parse(String input,int index){
        int endIndex = index+padding;
        if( endIndex > input.length() ){
            //长度不足
            return new Parser.Result(0,0L,false);
        }
        //有+号的时候贪婪匹配，往最长的数字去取
        if( this.allowOverPadding){
            while( endIndex < input.length() ){
                char c = input.charAt(endIndex);
                if( Character.isDigit(c)){
                    endIndex++;
                }else{
                    break;
                }
            }
        }
        //取数据
        String argumentValue = input.substring(index,endIndex).trim();
        try{
            Long argument = Long.valueOf(argumentValue);
            return new Parser.Result(endIndex,argument,true);
        }catch(NumberFormatException e){
            return new Parser.Result(0,0L,false);
        }
    }
}
