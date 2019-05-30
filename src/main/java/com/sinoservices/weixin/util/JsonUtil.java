package com.sinoservices.weixin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;

import java.lang.reflect.Type;

/**
 * @author Eason.Su
 * @Title: JsonUtil
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2916:45
 */
public class JsonUtil {

    public static String getJson(Object object){
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        String json = JSON.toJSONString(object, config);
        return json;
    }

    public static <T> T getObject(String json, Class classz){
        ParserConfig config = new ParserConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        T object = JSON.parseObject(json, classz,config);
        return object;
    }
}
