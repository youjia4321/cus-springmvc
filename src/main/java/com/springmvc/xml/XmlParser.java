package com.springmvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

public class XmlParser {

    public static String getBasePackage(String classpath) {
        try {
            SAXReader reader = new SAXReader();
            InputStream in = XmlParser.class.getClassLoader().getResourceAsStream(classpath);
            Document document = reader.read(in);
            Element root = document.getRootElement();
            Element componentScan = root.element("component-scan");
            Attribute attribute = componentScan.attribute("base-package");
            return attribute.getValue();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

}
