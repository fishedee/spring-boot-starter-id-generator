package com.fishedee.id_generator;

import org.springframework.stereotype.Component;

import java.util.Date;

public class DefaultCurrentTime implements CurrentTime{
    @Override
    public Date now(){
        return new Date();
    }
}
