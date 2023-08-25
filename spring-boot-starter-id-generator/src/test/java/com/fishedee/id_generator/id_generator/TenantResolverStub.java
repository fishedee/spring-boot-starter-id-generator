package com.fishedee.id_generator.id_generator;

import com.fishedee.id_generator.TenantResolver;

public class TenantResolverStub implements TenantResolver {

    private String tenantId = "0";

    public void setTenantId(String tenantId){
        this.tenantId = tenantId;
    }

    @Override
    public String getTenantId(){
        return this.tenantId;
    }
}
