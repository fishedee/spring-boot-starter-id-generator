package com.fishedee.id_generator;

public class DefaultTenantResolver implements TenantResolver{
    @Override
    public String getTenantId(){
        return "0";
    }
}
