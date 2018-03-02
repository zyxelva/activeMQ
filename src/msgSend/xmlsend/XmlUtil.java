package msgSend.xmlsend;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * xml杞琺ap锛宮ap杞瑇ml 甯﹀睘鎬�
 * http://happyqing.iteye.com/blog/2316275
 * @author happyqing
 * @since 2016.8.8
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class XmlUtil {
    public static void main(String[] args) throws DocumentException, IOException {
        //String textFromFile = FileUtils.readFileToString(new File("D:/workspace/workspace_3.7/xml2map/src/xml2json/sample.xml"),"UTF-8");
    	String str= "00000000<?xml version=\"1.0\" encoding=\"UTF-8\"?><cmd><response_code>0</response_code><text><text01>01</text01><text01>01</text01></text></cmd></xml>";
        str = str.substring(str.indexOf("<cmd>")).replace("</xml>", "");
        System.out.println(str);
    	Map<String, Object> map = xml2map(str, false);
        // long begin = System.currentTimeMillis();
        // for(int i=0; i<1000; i++){
        // map = (Map<String, Object>) xml2mapWithAttr(doc.getRootElement());
        // }
        //System.out.println("鑰楁椂:"+(System.currentTimeMillis()-begin));

    	//Map map = new HashMap();
		//map.put("txnName", "CUP_REMOTE_DIR");
        JSON json = JSONObject.fromObject(map);
        System.out.println(json.toString(1)); // 鏍煎紡鍖栬緭鍑�

       // Document doc = map2xml(map, "root");
        //Document doc = map2xml(map); //map涓惈鏈夋牴鑺傜偣鐨勯敭
        //System.out.println(formatXml(doc));
    }

    /**
     * xml杞琺ap 涓嶅甫灞炴��
     * @param xmlStr
     * @param needRootKey 鏄惁闇�瑕佸湪杩斿洖鐨刴ap閲屽姞鏍硅妭鐐归敭
     * @return
     * @throws DocumentException
     */
    public static Map xml2map(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = xml2map(root);
        if(root.elements().size()==0 && root.attributes().size()==0){
            return map;
        }
        if(needRootKey){
            //鍦ㄨ繑鍥炵殑map閲屽姞鏍硅妭鐐归敭锛堝鏋滈渶瑕侊級
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml杞琺ap 甯﹀睘鎬�
     * @param xmlStr
     * @param needRootKey 鏄惁闇�瑕佸湪杩斿洖鐨刴ap閲屽姞鏍硅妭鐐归敭
     * @return
     * @throws DocumentException
     */
    public static Map xml2mapWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = xml2mapWithAttr(root);
        if(root.elements().size()==0 && root.attributes().size()==0){
            return map; //鏍硅妭鐐瑰彧鏈変竴涓枃鏈唴瀹�
        }
        if(needRootKey){
            //鍦ㄨ繑鍥炵殑map閲屽姞鏍硅妭鐐归敭锛堝鏋滈渶瑕侊級
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml杞琺ap 涓嶅甫灞炴��
     * @param e
     * @return
     */
    private static Map xml2map(Element e) {
        Map map = new LinkedHashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else {
						map.put(iter.getName(), m);
					}
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else {
						map.put(iter.getName(), iter.getText());
					}
                }
            }
        } else {
			map.put(e.getName(), e.getText());
		}
        return map;
    }

    /**
     * xml杞琺ap 甯﹀睘鎬�
     * @param e
     * @return
     */
    private static Map xml2mapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        List<Element> list = element.elements();
        List<Attribute> listAttr0 = element.attributes(); // 褰撳墠鑺傜偣鐨勬墍鏈夊睘鎬х殑list
        for (Attribute attr : listAttr0) {
            map.put("@" + attr.getName(), attr.getValue());
        }
        if (list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                Element iter = list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2mapWithAttr(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else {
						map.put(iter.getName(), m);
					}
                } else {

                    List<Attribute> listAttr = iter.attributes(); // 褰撳墠鑺傜偣鐨勬墍鏈夊睘鎬х殑list
                    Map<String, Object> attrMap = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrMap = new LinkedHashMap<String, Object>();
                        for (Attribute attr : listAttr) {
                            attrMap.put("@" + attr.getName(), attr.getValue());
                        }
                    }

                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            // mapList.add(iter.getText());
                            if (hasAttributes && attrMap != null) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            // mapList.add(iter.getText());
                            if (hasAttributes && attrMap != null) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        // map.put(iter.getName(), iter.getText());
                        if (hasAttributes && attrMap != null) {
                            attrMap.put("#text", iter.getText());
                            map.put(iter.getName(), attrMap);
                        } else {
                            map.put(iter.getName(), iter.getText());
                        }
                    }
                }
            }
        } else {
            // 鏍硅妭鐐圭殑
            if (listAttr0.size() > 0) {
                map.put("#text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    /**
     * map杞瑇ml map涓病鏈夋牴鑺傜偣鐨勯敭
     * @param map
     * @param rootName
     * @throws DocumentException
     * @throws IOException
     */
    public static Document map2xml(Map<String, Object> map, String rootName) throws DocumentException, IOException  {
        Document doc = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement(rootName);
        doc.add(root);
        map2xml(map, root);
        //System.out.println(doc.asXML());
        //System.out.println(formatXml(doc));
        return doc;
    }

    /**
     * map杞瑇ml map涓惈鏈夋牴鑺傜偣鐨勯敭
     * @param map
     * @throws DocumentException
     * @throws IOException
     */
    public static Document map2xml(Map<String, Object> map) throws DocumentException, IOException  {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        if(entries.hasNext()){ //鑾峰彇绗竴涓敭鍒涘缓鏍硅妭鐐�
            Map.Entry<String, Object> entry = entries.next();
            Document doc = DocumentHelper.createDocument();
            Element root = DocumentHelper.createElement(entry.getKey());
            doc.add(root);
            map2xml((Map)entry.getValue(), root);
            //System.out.println(doc.asXML());
            //System.out.println(formatXml(doc));
            return doc;
        }
        return null;
    }

    /**
     * map杞瑇ml
     * @param map
     * @param body xml鍏冪礌
     * @return
     */
    private static Element map2xml(Map<String, Object> map, Element body) {
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.startsWith("@")){    //灞炴��
                body.addAttribute(key.substring(1, key.length()), value.toString());
            } else if(key.equals("#text")){ //鏈夊睘鎬ф椂鐨勬枃鏈�
                body.setText(value.toString());
            } else {
                if(value instanceof java.util.List ){
                    List list = (List)value;
                    Object obj;
                    for(int i=0; i<list.size(); i++){
                        obj = list.get(i);
                        //list閲屾槸map鎴朣tring锛屼笉浼氬瓨鍦╨ist閲岀洿鎺ユ槸list鐨勶紝
                        if(obj instanceof java.util.Map){
                            Element subElement = body.addElement(key);
                            map2xml((Map)list.get(i), subElement);
                        } else {
                            body.addElement(key).setText((String)list.get(i));
                        }
                    }
                } else if(value instanceof java.util.Map ){
                    Element subElement = body.addElement(key);
                    map2xml((Map)value, subElement);
                } else {
                	if(value != null ){
                        body.addElement(key).setText(value.toString());
                	} else {
                        body.addElement(key).setText("");
                	}
                }
            }
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        return body;
    }

    /**
     * 鏍煎紡鍖栬緭鍑簒ml
     * @param xmlStr
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static String formatXml(String xmlStr) throws DocumentException, IOException  {
        Document document = DocumentHelper.parseText(xmlStr);
        return formatXml(document);
    }

    /**
     * 鏍煎紡鍖栬緭鍑簒ml
     * @param document
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static String formatXml(Document document) throws DocumentException, IOException  {
        // 鏍煎紡鍖栬緭鍑烘牸寮�
        OutputFormat format = OutputFormat.createPrettyPrint();
        //format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        // 鏍煎紡鍖栬緭鍑烘祦
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        // 灏哾ocument鍐欏叆鍒拌緭鍑烘祦
        xmlWriter.write(document);
        xmlWriter.close();
        return writer.toString();
    }

}
