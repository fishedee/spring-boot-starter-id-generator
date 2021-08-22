package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;
import org.apache.logging.log4j.util.Strings;

public class PaddingRender implements Render {
    private int padding;

    public PaddingRender(int padding) {
        if (padding <= 0) {
            throw new IdGeneratorException(1, "左补齐不能为负数[" + padding + "]", null);
        }
        this.padding = padding;
    }

    @Override
    public String render(Long arg) {
        String input = arg.toString();
        if (input.length() > padding) {
            throw new IdGeneratorException(1,"数字长度超过padding:"+padding+",["+input+"]",null);
        }
        int paddingLength = padding - input.length();
        return Strings.repeat("0", paddingLength) + input;
    }
}
