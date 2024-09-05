package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

public class PlaceTemplate {

    private char beginDelimiter;

    private char endDelimiter;

    private String tpl;

    private List<Node> tplNodes;

    private Map<String,Render> rewriteRenders;

    private Map<String,Parser> rewriteParsers;

    public PlaceTemplate(String tpl){
        this.tpl = tpl;
        this.beginDelimiter = '{';
        this.endDelimiter = '}';
        this.rewriteRenders = new HashMap<>();
        this.rewriteParsers = new HashMap<>();

    }

    private void init(){
        if( this.tplNodes == null ){
            this.tplNodes = this.compile();
            this.rewrite();
        }
    }

    public void addRender(String placeholder,Render render){
        this.rewriteRenders.put(placeholder,render);
    }

    public void addParser(String placeholder,Parser parser){
        this.rewriteParsers.put(placeholder,parser);
    }

    public void setBeginDelimiter(char delimiter){
        this.beginDelimiter = delimiter;
    }

    public void setEndDelimiter(char delimiter){
        this.endDelimiter = delimiter;
    }

    private void rewrite(){
        for( Node node :this.tplNodes){
            if( node.getType() != Node.NodeType.PLACEHOLDER){
                continue;
            }

            String placehodler = node.getPlaceholder();

            //重置render
            Render render = this.rewriteRenders.get(placehodler);
            if( render != null ){
                node.setRender(render);
            }

            //重置parser
            Parser parser = this.rewriteParsers.get(placehodler);
            if( parser != null ){
                node.setParser(parser);
            }
        }
    }

    private boolean isExistInNodes(List<Node> nodes,String placeholder){
        for( Node node :nodes ){
            if( node.getType() == Node.NodeType.PLACEHOLDER &&
                node.getPlaceholder().equals(placeholder)){
                return true;
            }
        }
        return false;
    }

    private List<Node> compile(){
        List<Node> tplNodes = new ArrayList<>();
        String input = this.tpl;
        int i = 0;
        while( i < input.length() ){
            int beginIndex = input.indexOf(this.beginDelimiter,i);
            if( beginIndex < 0 ){
                //没有找到左节点
                tplNodes.add(Node.Literal(input.substring(i)));
                break;
            }
            int endIndex = input.indexOf(this.endDelimiter,beginIndex+1);
            if( endIndex < 0 ){
                throw new IdGeneratorException(1,"模板格式错误["+tpl+"]，没有匹配的右括号",null);
            }
            //提取字面值
            String literal = input.substring(i,beginIndex);
            if( literal.length()!=0){
                tplNodes.add(Node.Literal(literal));
            }

            //提取占位符
            String placeholder = input.substring(beginIndex+1,endIndex);
            i = endIndex + 1;
            if( Strings.isBlank(placeholder)){
                throw new IdGeneratorException(1,"模板格式错误["+tpl+"]，占位符为空",null);
            }

            Node placeHolderNode = Node.PlaceHolder(placeholder.trim());
            if( isExistInNodes(tplNodes,placeHolderNode.getPlaceholder())){
                throw new IdGeneratorException(1,"模板格式错误，重复的占位符:["+placeHolderNode.getPlaceholder()+"]",null);
            }
            tplNodes.add(placeHolderNode);
        }
        return tplNodes;
    }

    private boolean isMatchLiteral(String message,int index,String target){
        int endIndex = target.length()+index;
        if( endIndex > message.length()){
            return false;
        }

        String substr = message.substring(index,endIndex);
        return substr.equals(target);
    }

    public String getTemplate(){
        return this.tpl;
    }

    public Param extractParam(String message){
        this.init();
        Param result = new Param();
        //初始化每个参数
        for( Node node :tplNodes){
            if( node.getType() == Node.NodeType.PLACEHOLDER){
                result.put(node.getPlaceholder(),null);
            }
        }

        //开始逐个匹配
        int index = 0;
        for( int j = 0 ;j < tplNodes.size();j++){
            Node node = tplNodes.get(j);
            if( node.getType() == Node.NodeType.LITERAL){
                //字面值
                String literal = node.getLiteralValue();
                boolean isMatch = isMatchLiteral(message,index,literal);
                if( isMatch == false ){
                    //不匹配，直接提前结束
                    return result;
                }
                //匹配的话往前走
                index = index+literal.length();
            }else if( node.getType() == Node.NodeType.PLACEHOLDER){
                //占位符
                Parser.Result parserResult = node.getParser().parse(message,index);
                if( parserResult.isSuccess() == false ){
                    //不匹配，直接提前结束
                    return result;
                }
                result.put(node.getPlaceholder(),parserResult.getArgument());
                //匹配的话往前走
                index = parserResult.getNextIndex();
            }else{
                //what the fuck is here
                throw new RuntimeException("未知的nodeType:["+node.getType()+"]");
            }
        }
        return result;
    }

    public String format(Param param){
        this.init();
        StringBuilder builder = new StringBuilder();
        for( Node node :this.tplNodes){
            if( node.getType() == Node.NodeType.LITERAL){
                builder.append(node.getLiteralValue());
            }else if( node.getType() == Node.NodeType.PLACEHOLDER){
                Long argument = param.get(node.getPlaceholder());
                if( argument == null ){
                    throw new IdGeneratorException(1,"缺少模板参数:"+node.getPlaceholder(),null);
                }
                Render render = node.getRender();
                builder.append(render.render(argument));
            }else{
                //what the fuck is here
                throw new RuntimeException("未知的nodeType:["+node.getType()+"]");
            }
        }
        return builder.toString();
    }

    private String quoteRegexSpecialCharacters(String input){
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ;i != input.length();i++){
            char c = input.charAt(i);
            if( c == '{' ||
                c == '}' ||
                c == '+' ||
                c == '*' ||
                c == '[' ||
                c == ']' ||
                c == '(' ||
                c == ')' ){
                builder.append("\\");
            }
            builder.append(c);
        }
        return builder.toString();
    }
    public String calcMatchIdRegex(Param param){
        this.init();
        //只有id参数需要转义为\d+，其他字符都是原样复制
        StringBuilder builder = new StringBuilder();
        for( Node node :this.tplNodes){
            if( node.getType() == Node.NodeType.LITERAL){
                builder.append(this.quoteRegexSpecialCharacters(node.getLiteralValue()));
            }else if( node.getType() == Node.NodeType.PLACEHOLDER){
                Long argument = param.get(node.getPlaceholder());
                if( argument == null ){
                    throw new IdGeneratorException(1,"缺少模板参数:"+node.getPlaceholder(),null);
                }
                if( node.getPlaceholder().equals("id")){
                    builder.append("(\\d+)");
                }else{
                    Render render = node.getRender();
                    builder.append(this.quoteRegexSpecialCharacters(render.render(argument)));
                }
            }else{
                //what the fuck is here
                throw new RuntimeException("未知的nodeType:["+node.getType()+"]");
            }
        }
        return builder.toString();
    }
}
