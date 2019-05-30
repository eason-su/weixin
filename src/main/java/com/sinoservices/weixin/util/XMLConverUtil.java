package com.sinoservices.weixin.util;

/**
 * @author Eason.Su
 * @Title: XMLConverUtil
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2819:31
 */

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XML 数据接收对象转换工具类
 * @author LiYi
 *
 */
public class XMLConverUtil{

    private static final ThreadLocal<Map<Class<?>,Marshaller>> mMapLocal = new ThreadLocal<Map<Class<?>,Marshaller>>() {
        @Override
        protected Map<Class<?>, Marshaller> initialValue() {
            return new HashMap<Class<?>, Marshaller>();
        }
    };

    private static final ThreadLocal<Map<Class<?>,Unmarshaller>> uMapLocal = new ThreadLocal<Map<Class<?>,Unmarshaller>>(){
        @Override
        protected Map<Class<?>, Unmarshaller> initialValue() {
            return new HashMap<Class<?>, Unmarshaller>();
        }
    };

    /**
     * XML to Object
     * @param <T> T
     * @param clazz clazz
     * @param xml xml
     * @return T
     */
    public static <T> T convertToObject(Class<T> clazz,String xml){
        return convertToObject(clazz,new StringReader(xml));
    }

    /**
     * XML to Object
     * @param <T> T
     * @param clazz clazz
     * @param inputStream  inputStream
     * @return T
     */
    public static <T> T convertToObject(Class<T> clazz,InputStream inputStream){
        return convertToObject(clazz,new InputStreamReader(inputStream));
    }

    /**
     * XML to Object
     * @param <T> T
     * @param clazz clazz
     * @param reader reader
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToObject(Class<T> clazz,Reader reader){
        try {
            Map<Class<?>, Unmarshaller> uMap = uMapLocal.get();
            if(!uMap.containsKey(clazz)){
                JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                uMap.put(clazz, unmarshaller);
            }
            return (T) uMap.get(clazz).unmarshal(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object to XML
     * @param object object
     * @return xml
     */
    public static String convertToXML(Object object){
        try {
            Map<Class<?>, Marshaller> mMap = mMapLocal.get();
            if(!mMap.containsKey(object.getClass())){
                JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                //设置CDATA输出字符
                marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
                    @Override
                    public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException {
                        writer.write(ac, i, j);
                    }
                });
                mMap.put(object.getClass(), marshaller);
            }
            StringWriter stringWriter = new StringWriter();
            mMap.get(object.getClass()).marshal(object,stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换简单的xml to map
     * @param xml xml
     * @return map
     */
    public static Map<String,String> convertToMap(String xml){
        Map<String, String> map = new LinkedHashMap<String,String>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            if(root != null){
                NodeList childNodes = root.getChildNodes();
                if(childNodes != null && childNodes.getLength()>0){
                    for(int i = 0;i < childNodes.getLength();i++){
                        Node node = childNodes.item(i);
                        if( node != null && node.getNodeType() == Node.ELEMENT_NODE){
                            map.put(node.getNodeName(), node.getTextContent());
                        }
                    }
                }
            }
        } catch (DOMException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        String str = "<xml><AppId><![CDATA[wxcad1498aa776c5e3]]></AppId>\n" +
                "<CreateTime>1559061085</CreateTime>\n" +
                "<InfoType><![CDATA[component_verify_ticket]]></InfoType>\n" +
                "<ComponentVerifyTicket><![CDATA[ticket@@@dZalYYclLPGi047KG9udX58cMcJrjXkPfy3bl6INoiLFjU3AnetCIcKYWNlOnWosWyJKMtU-EVjxGsnFpsbjjQ]]></ComponentVerifyTicket>\n" +
                "</xml>";

        Map<String,String> map = convertToMap(str);
        System.out.println(map);
    }
}
