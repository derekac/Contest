package com.tzc.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {
	private static Logger logger = LogUtil.getLogger();

	public static String getValueByIDFromConfig(String id) {
		try {
			String configXmlPath = Utils.getProjectAbsolutePath()
					+ "config.xml";
			InputStream source = new FileInputStream(configXmlPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(source);
			Node root = doc.getFirstChild();
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.hasAttributes()) {
					NamedNodeMap attr = n.getAttributes();
					Node idNode = attr.getNamedItem("id");
					if (idNode != null && idNode.getNodeValue().equals(id)) {
						String value = attr.getNamedItem("value")
								.getNodeValue();
						return value;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getProjectAbsolutePath() {
		String path = Utils.class.getResource("/").getPath();
		try {
			path = URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return path;
	}
	
    public static String getValueByIDFromXML(String id) {
        try {
                String configXmlPath = Utils.getProjectAbsolutePath() + "config.xml";
                InputStream source = new FileInputStream(configXmlPath);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(source);
                Node root = doc.getFirstChild();
                NodeList list = root.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                        Node n = list.item(i);
                        if (n.hasAttributes()) {
                                NamedNodeMap attr = n.getAttributes();
                                Node idNode = attr.getNamedItem("id");
                                if (idNode != null && idNode.getNodeValue().equals(id)) {
                                        String value = attr.getNamedItem("value").getNodeValue();
                                        return value;
                                }
                        }
                }
                return null;
        } catch (Exception e) {
                return null;
        }
}
}
