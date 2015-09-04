package com.wemote.scorpio.modules.utils.xml;

import com.thoughtworks.xstream.XStream;

/**
 * @author: jayon.xu@gmail.com
 */
public class XMLUtils {

    private XMLUtils() {
    }

    /**
     * XStream，使其支持CDATA块
     */
    private static XStream xstream = XStreamInitializer.getInstance();
    /*private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });*/

    /**
     * java 转换成xml
     *
     * @param obj 对象实例
     * @return String xml字符串
     */
    public static String toXML(Object obj) {
        xstream.processAnnotations(obj.getClass());
        return xstream.toXML(obj);
    }

    /**
     * 将传入xml文本转换成Java对象
     * 调用的方法实例：PersonBean person=XmlUtil.toBean(xmlStr, PersonBean.class);
     *
     * @param xmlStr xml字符串
     * @param cls    xml对应的class类
     * @return T xml对应的class类的实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String xmlStr, Class<T> cls) {
        //XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
        return (T) xstream.fromXML(xmlStr);
    }
}
