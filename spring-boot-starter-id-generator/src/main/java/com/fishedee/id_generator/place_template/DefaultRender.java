package com.fishedee.id_generator.place_template;

public class DefaultRender implements Render{
    public DefaultRender() {
    }

    @Override
    public String render(Long arg) {
        return arg.toString();
    }
}
