package com.fishedee.id_generator.place_template;

public class DefaultParser implements Parser{
    public DefaultParser(){

    }

    @Override
    public Parser.Result parse(String input,int index){
        int i = index;
        for( ;i < input.length();i++){
            if( input.charAt(i) >= '0' && input.charAt(i) <= '9'){
                continue;
            }
            //非数字直接退出
            break;
        }
        String argumentValue = input.substring(index,i).trim();
        try{
            Long argument = Long.valueOf(argumentValue);
            return new Parser.Result(i,argument,true);
        }catch(NumberFormatException e){
            return new Parser.Result(0,0L,false);
        }
    }
}
