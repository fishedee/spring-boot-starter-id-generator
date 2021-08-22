package com.fishedee.id_generator;

public class IdGeneratorException extends RuntimeException{
    public IdGeneratorException(int code, String message, Object data){
        super("id Generator fail: ["+message+"]");
    }
}
