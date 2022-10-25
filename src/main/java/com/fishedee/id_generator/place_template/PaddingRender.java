package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;
import org.apache.logging.log4j.util.Strings;

public class PaddingRender implements Render {
    private boolean allowOverPadding;

    private int padding;

    public PaddingRender(boolean allowOverPadding,int padding) {
        if (padding <= 0) {
            throw new IdGeneratorException(1, "左补齐不能为负数[" + padding + "]", null);
        }
        this.allowOverPadding = allowOverPadding;
        this.padding = padding;
    }

    @Override
    public String render(Long arg) {
        String input = arg.toString();
        if( input.length() <= padding ){
            //需补0
            int paddingLength = padding - input.length();
            return Strings.repeat("0", paddingLength) + input;
        }else{
            //超过长度
            if( this.allowOverPadding) {
                //允许超出
                return input;
            }else{
                //不允许超出
                throw new IdGeneratorException(1,"数字长度超过padding:"+padding+",["+input+"]",null);
            }
        }
    }
}
