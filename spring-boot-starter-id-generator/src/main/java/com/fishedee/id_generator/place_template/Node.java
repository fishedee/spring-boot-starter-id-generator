package com.fishedee.id_generator.place_template;

import lombok.Data;

@Data
public class Node {
    public enum NodeType {
        LITERAL,
        PLACEHOLDER;
    }

    private NodeType type;

    private String placeholder;

    private String literalValue;

    private Render render;

    private Parser parser;

    public static Node Literal(String input) {
        Node result = new Node();
        result.type = NodeType.LITERAL;
        result.literalValue = input;
        return result;
    }

    public static Node PlaceHolder(String placeholder) {
        //解析参数
        String option = "";
        int argumentIndex = placeholder.indexOf(':');
        if (argumentIndex != -1) {
            option = placeholder.substring(argumentIndex + 1).trim();
            placeholder = placeholder.substring(0, argumentIndex).trim();
        }

        Render render = RenderFactory.getRender(option);
        Parser parser = ParserFactory.getParser(option);


        //得到解
        Node result = new Node();
        result.type = NodeType.PLACEHOLDER;
        result.placeholder = placeholder;
        result.render = render;
        result.parser = parser;
        return result;
    }
}
