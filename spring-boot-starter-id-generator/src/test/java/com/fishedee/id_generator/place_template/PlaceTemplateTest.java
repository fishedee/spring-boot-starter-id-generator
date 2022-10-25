package com.fishedee.id_generator.place_template;

import com.fishedee.id_generator.IdGeneratorException;
import com.fishedee.id_generator.JsonAssertUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceTemplateTest {
    @Test
    public void empty(){
        PlaceTemplate template = new PlaceTemplate("");
        String result = template.format(new Param());
        assertEquals("",result);
    }

    @Test
    public void litral(){
        PlaceTemplate template = new PlaceTemplate("ACV-we");

        //format
        String result = template.format(new Param());
        assertEquals("ACV-we",result);

        //extractParam
        Param param = template.extractParam("ACV-we");
        JsonAssertUtil.checkEqualStrict(
                "{}",
                param
        );
    }

    @Test
    public void placeholder(){
        PlaceTemplate template = new PlaceTemplate("ACV{year}");

        //format
        Param param = new Param();
        param.put("year",17L);
        String result = template.format(param);
        assertEquals("ACV17",result);

        //extractParam
        Param param2 = template.extractParam("ACV89");
        JsonAssertUtil.checkEqualStrict(
                "{year:89}",
                param2
        );

        Param param3 = template.extractParam("ACV999MK");
        JsonAssertUtil.checkEqualStrict(
                "{year:999}",
                param3
        );
    }

    @Test
    public void placeholder_padding(){
        PlaceTemplate template = new PlaceTemplate("ACV{ year : 6 }{month:4}k{id:}_A3");

        //format
        Param param = new Param();
        param.put("year",1L);
        param.put("month",2L);
        param.put("id",78L);
        String result = template.format(param);
        assertEquals("ACV0000010002k78_A3",result);

        //extractParam
        Param param2 = template.extractParam("ACV0030017002k0123123_A3");
        JsonAssertUtil.checkEqualStrict(
                "{year:3001,month:7002,id:123123}",
                param2
        );
    }

    @Test
    public void placeholder_overPadding(){
        PlaceTemplate template = new PlaceTemplate("ACV{ id :  4   + }cg{year:2+}");
        //format
        Param param = new Param();
        param.put("id",1234L);
        param.put("year",56L);
        String result = template.format(param);
        assertEquals("ACV1234cg56",result);

        //extractParam
        Param param_o = template.extractParam(result);
        JsonAssertUtil.checkEqualStrict(
                "{id:1234,year:56}",
                param_o
        );

        //format2
        Param param2 = new Param();
        param2.put("id",23L);
        param2.put("year",1L);
        String result2 = template.format(param2);
        assertEquals("ACV0023cg01",result2);

        //extractParam2
        Param param2_o = template.extractParam(result2);
        JsonAssertUtil.checkEqualStrict(
                "{id:23,year:1}",
                param2_o
        );

        //format3
        Param param3 = new Param();
        param3.put("id",12345678L);
        param3.put("year",987654321L);
        String result3 = template.format(param3);
        assertEquals("ACV12345678cg987654321",result3);

        //extractParam3
        Param param3_o = template.extractParam(result3);
        JsonAssertUtil.checkEqualStrict(
                "{id:12345678,year:987654321}",
                param3_o
        );
    }

    @Test
    public void placeholder_last(){
        PlaceTemplate template = new PlaceTemplate("CC {year}");

        //format
        Param param = new Param();
        param.put("year",1234L);
        String result = template.format(param);
        assertEquals("CC 1234",result);

        //extractParam
        Param param2 = template.extractParam("CC ");
        JsonAssertUtil.checkEqualStrict(
                "{year:null}",
                param2
        );

        Param param3 = template.extractParam("CC 789");
        JsonAssertUtil.checkEqualStrict(
                "{year:789}",
                param3
        );

        Param param4 = template.extractParam("CC");
        JsonAssertUtil.checkEqualStrict(
                "{year:null}",
                param4
        );

        Param param5 = template.extractParam("CC 0");
        JsonAssertUtil.checkEqualStrict(
                "{year:0}",
                param5
        );
    }

    @Test
    public void resetRenderAndParser(){
        PlaceTemplate template = new PlaceTemplate("{year}{day}");
        template.addParser("year",new PaddingParser(false,4));
        template.addRender("year",new PaddingRender(false,4));
        template.addParser("day",new PaddingParser(false,2));
        template.addRender("day",new PaddingRender(false,2));

        //format
        Param param = new Param();
        param.put("year",1L);
        param.put("day",2L);
        String message = template.format(param);
        assertEquals("000102",message);

        param = template.extractParam("123456");
        JsonAssertUtil.checkEqualStrict(
                "{year:1234,day:56}",
                param
        );
    }

    @Test
    public void resetBeginAndEndDelimeter(){
        PlaceTemplate template = new PlaceTemplate("AX[year]U");
        template.setBeginDelimiter('[');
        template.setEndDelimiter(']');

        //extract
        Param param = template.extractParam("AX123U");
        JsonAssertUtil.checkEqualStrict(
                "{year:123}",
                param
        );

        //format
        Param param2 = new Param();
        param2.put("year",10L);
        String message = template.format(param2);
        assertEquals("AX10U",message);
    }

    @Test
    public void whenLiteralMatchFail(){
        PlaceTemplate placeTemplate = new PlaceTemplate("{year:4}AK");

        Param param = placeTemplate.extractParam("1001");
        JsonAssertUtil.checkEqualStrict(
                "{year:1001}",
                param
        );
    }

    @Test
    public void whenNoneMatch(){
        PlaceTemplate placeTemplate = new PlaceTemplate("BG{year:4}AK{day}");

        Param param = placeTemplate.extractParam("B");
        JsonAssertUtil.checkEqualStrict(
                "{year:null,day:null}",
                param
        );
    }

    @Test
    public void whenFirstPlaceholderNonMatch(){
        PlaceTemplate placeTemplate = new PlaceTemplate("BG{year:4}AK{day}");

        Param param = placeTemplate.extractParam("BGKCAK");
        JsonAssertUtil.checkEqualStrict(
                "{year:null,day:null}",
                param
        );
    }

    @Test
    public void whenSecondPlaceholderNonMatch(){
        PlaceTemplate placeTemplate = new PlaceTemplate("BG{year:4}AK{day}");

        Param param = placeTemplate.extractParam("BG1001AK");
        JsonAssertUtil.checkEqualStrict(
                "{year:1001,day:null}",
                param
        );
    }

    @Test
    public void whenPlaceHolderEmpty(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("BG{}AK");
            placeTemplate.extractParam("");
        });
        assertTrue(e.getMessage().contains("占位符为空"));
    }

    @Test
    public void whenPlaceHolderNotClose(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("bG{a");
            placeTemplate.extractParam("");
        });
        assertTrue(e.getMessage().contains("没有匹配的右括号"));
    }

    @Test
    public void whenPlaceHolderDuplicate(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("bG{year}{day}{ year : 5 }cs");
            placeTemplate.extractParam("");
        });
        assertTrue(e.getMessage().contains("重复的占位符:[year]"));
    }

    @Test
    public void whenPlaceHolderArgumentFail(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("BG{:CD}AK");
            placeTemplate.extractParam("");
        });
        assertTrue(e.getMessage().contains("模板参数错误"));
    }

    @Test
    public void whenPlaceHolderArgumentNotPositive(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("BG{:-1}AK");
            placeTemplate.extractParam("");
        });
        assertTrue(e.getMessage().contains("补齐不能为负数"));
    }

    @Test
    public void whenPaddingToLong(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("BG{year:2}AK");

            Param param = new Param();
            param.put("year",234L);
            placeTemplate.format(param);
        });
        assertTrue(e.getMessage().contains("数字长度超过padding"));
    }

    @Test
    public void whenLackOfArgument(){
        IdGeneratorException e = assertThrows(IdGeneratorException.class,()->{
            PlaceTemplate placeTemplate = new PlaceTemplate("BG{year:2}AK");

            Param param = new Param();
            placeTemplate.format(param);
        });
        assertTrue(e.getMessage().contains("缺少模板参数"));
    }
}
