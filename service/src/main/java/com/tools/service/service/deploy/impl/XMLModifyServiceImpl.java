package com.tools.service.service.deploy.impl;

import com.tools.service.service.deploy.IConfigModifyService;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * xml配置修改
 */
public class XMLModifyServiceImpl implements IConfigModifyService {
    private static final Logger log = LoggerFactory.getLogger(XMLModifyServiceImpl.class);
    @Override
    public void configModifying(String filePath, LinkedHashMap<String, String> linkedHashMap) {

        File file = new File(filePath);
        if (!file.exists()) {
            log.error("cm配置文件 " + filePath + "不存在");
            return;
        }
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(file);
            //获取根节点
            Element rootElement = document.getRootElement();

            //检查xml是否存在命名空间
            String namespaceURI = rootElement.getNamespaceURI();
            HashMap<String, String> namespaceMap = null;
            if (!namespaceURI.isEmpty()) {
                namespaceMap = new HashMap<>();
                namespaceMap.put("nameSpace", namespaceURI);
            }
            //修改的项为空则直接返回
            if (linkedHashMap.entrySet().size() < 1) {
                return;
            }
            //遍历修改配置
            for (Map.Entry<String, String> stringStringEntry : linkedHashMap.entrySet()) {
                //配置项名称
                String key = stringStringEntry.getKey();
                //配置项修改的值
                String value = stringStringEntry.getValue();
                //获取父标签名字
                String rootElementPath = rootElement.getName();
                //处理没有命名空间的xml
                if (null == namespaceMap) {
                    //搜索配置项 标签值
                    List<Node> nodes = rootElement.selectNodes("/" + rootElementPath + "//" + key);
                    //若配置项不存在则搜索 属性值
                    if (nodes.size() < 1) {
                        //搜索是否是属性值
                        nodes = rootElement.selectNodes("/" + rootElementPath + "//*[@*='" + key + "']");
                        if (nodes.size() >= 1) {
                            Element element = (Element) nodes.get(0);
                            List<Attribute> attributes = element.attributes();
                            //属性 配置项名字是否存在
                            boolean isExist = false;
                            for (Attribute attribute : attributes) {

                                if (attribute.getValue().equals(key)) {
                                    //如果属性名字为属性值时，标记
                                    isExist = true;
                                }
                                //循环遍历为从左到有 如<setting name="logImpl" value="STDOUT_LOGGING"></setting>
                                //先表述属性值 name是否存在。存在则去修改右边value
                                // TODO: 2019/3/7 目前只处理属性值名称为value
                                if (isExist) {
                                    if (attribute.getName().equals("value")) {
                                        log.warn(filePath + " Element:" + element.getName() + "attribute name:" + attribute.getName() + "default value:" + attribute.getValue() + " changed value:" + value);
                                        //修改值
                                        attribute.setValue(value);
                                    }
                                }
                            }
                        } else if (nodes.size() < 1) {
                            log.warn(filePath + " Element:" + key + "not found!");
                        }
                    } else {
                        //若配置项不存在则修改配置项
                        Element element = (Element) nodes.get(0);
                        log.warn(filePath + " Element:" + key + "default value:" + element.getText() + " changed value:" + value);
                        element.setText(value);
                    }
                } else {
                    //处理存在命名空间的xml
                    // TODO: 2019/2/14 处理要修改的值为xml标签属性值，如果多个标签属性key相同时，支取第一个，暂未处理多个 ,后续加上内容值判断来确定是那个值

                    // TODO: 2019/2/14 xml中会存在多个相同的标签值 暂未处理 ，每次只取第一个处理。后续加上内容值判断来确定是那个值
                    //如果此key为不是xml属性值 则查询是否为xml标签
                    XPath xPath = document.createXPath("/nameSpace:" + rootElementPath + "//nameSpace:" + key);
                    xPath.setNamespaceURIs(namespaceMap);
                    //搜索
                    List<Node> nodes = xPath.selectNodes(document);
                    if (nodes.size() == 1) {
                        Element element = (Element) nodes.get(0);
                        element.setText(value);
                        log.warn(filePath + " Element:" + key + "default value:" + element.getText() + " changed value:" + value);
                    } else if (nodes.size() < 1) {

                        //搜索标签不存在的话 查询此key是否为xml属性值
                        xPath = document.createXPath("/nameSpace:" + rootElementPath + "//nameSpace:*[@*='" + key + "']");
                        xPath.setNamespaceURIs(namespaceMap);

                        nodes = xPath.selectNodes(document);
                        //如果此key为xml属性值 则修改属性值
                        if (nodes.size() == 1) {
                            Element element = (Element) nodes.get(0);
                            //设置属性值
                            List<Attribute> attributes = element.attributes();
                            //只处理了 属性值为 value 的 属性修改
                            boolean isExist = false;
                            for (Attribute attribute : attributes) {
                                if (attribute.getValue().equals(key)) {
                                    isExist = true;
                                }
                                if (isExist) {
                                    if (attribute.getName().equals("value")) {
                                        log.warn(filePath + " Element:" + element.getName() + "attribute name:" + attribute.getName() + "default value:" + attribute.getValue() + " changed value:" + value);
                                        attribute.setValue(value);
                                    }
                                }
                            }
                        } else if (nodes.size() < 1) {
                            log.warn(filePath + " Element:" + key + "not found!");
                        }

                    } else if (nodes.size() >= 2) {
                        log.warn(filePath + "attribute name:" + key + "multiple identical records");
                    }
                }
                //
            }
            saveXml(filePath, document);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveXml(String xmlPath, Document document) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(xmlPath);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, Charset.forName("utf-8"));
        org.dom4j.io.OutputFormat compactFormat = org.dom4j.io.OutputFormat.createCompactFormat();
        compactFormat.setEncoding("utf-8");
        compactFormat.setIndent(true);
        compactFormat.setIndent("      ");
        //是否限制xml头部文件  false 显示
        compactFormat.setOmitEncoding(false);
        compactFormat.setPadText(true);
        compactFormat.setXHTML(true);
        compactFormat.setNewlines(true);
        //设置空节点不隐藏  from <tagName> to <tagName></tagName>.
        compactFormat.setExpandEmptyElements(true);
        XMLWriter xmlWriter = new XMLWriter(outputStreamWriter, compactFormat);
        xmlWriter.write(document);
        xmlWriter.flush();
        xmlWriter.close();
        outputStreamWriter.close();
        fileOutputStream.close();
    }

}
