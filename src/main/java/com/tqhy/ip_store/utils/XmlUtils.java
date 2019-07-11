package com.tqhy.ip_store.utils;

import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.models.xml.biblio.Biblio;
import com.tqhy.ip_store.models.xml.fulltext.Fulltext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.util.*;

/**
 * xml工具类,单例.
 *
 * @author Yiheng
 * @create 2018/12/11
 * @since 1.0.0
 */
public class XmlUtils {

    static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * 获所有xml文件所在文件夹集合的迭代器
     *
     * @param baseDirPath       String,xml文件根路径
     * @param biblioBasePathArr 数组,元素为不同专利类型文件夹名称
     * @return Iterator&lt;File>&gt;
     */
    public static Iterator<File> getAllXmlDirUnderBaseDir(String baseDirPath, String[] biblioBasePathArr) {
        return Arrays.stream(biblioBasePathArr)
                     .collect(ArrayList<File>::new,
                              (list, biblioBasePath) -> {
                                  File workBiblioDir = new File(baseDirPath, biblioBasePath);
                                  if (workBiblioDir.exists()) {
                                      list.addAll(getXmlDirsUnderBiblioBaseDir(workBiblioDir));
                                  }
                              },
                              ArrayList::addAll)
                     .iterator();
    }

    /**
     * 获取某类型专利文件夹下所有专利xml文档所在文件夹集合
     *
     * @param workBiblioDir 某类型专利文件夹的{@link File}对象
     * @return List&lt;File&gt; 元素为xml所在文件夹的{@link File}对象
     */
    public static List<File> getXmlDirsUnderBiblioBaseDir(File workBiblioDir) {

        File[] yearDirs = workBiblioDir.listFiles(File::isDirectory);
        logger.info("biblio dirs in {} collection start...", workBiblioDir.getAbsolutePath());
        ArrayList<File> xmlDirs = Arrays.stream(yearDirs)
                                        .collect(ArrayList::new,
                                                 (list, yearDir) -> {
                                                     File[] xmlDirArr = XmlUtils.getXmlDirsUnderYearDir(yearDir);
                                                     list.addAll(Arrays.asList(xmlDirArr));
                                                 },
                                                 ArrayList::addAll);
        logger.info("biblio dirs in {} collection complete...", workBiblioDir.getAbsolutePath());
        return xmlDirs;
    }

    /**
     * 获取日期文件夹下所有xml文件夹数组
     *
     * @param yearDir xml文件所在的年月日文件夹的{@link File}对象
     * @return
     */
    public static File[] getXmlDirsUnderYearDir(File yearDir) {
        String yearDirName = yearDir.getName();
        String parentDirPath = "CREATE/" + yearDirName + "/";
        File parentDir = new File(yearDir, parentDirPath);
        return parentDir.listFiles(File::isDirectory);
    }


    /**
     * 从xml文件获取{@link RawDoc}对象
     *
     * @param xmlDir 著录项xml所在文件夹的{@link File}对象
     * @return
     */
    public static Optional<RawDoc> getRawDocFromXml(File xmlDir) {
        String xmlName = xmlDir.getName();
        String xmlFileName = xmlName + ".XML";
        logger.info("parse xml: {}", xmlDir.getAbsolutePath());
        File biblioXmlFile = new File(xmlDir, xmlFileName);

        //logger.info("biblio xml file is: " + biblioXmlFile.getAbsolutePath());
        String fulltextXmlPath = biblioXmlFile.getAbsolutePath().replace("BIBLIOGRAPHIC", "FULLTEXT");
        File fulltextXmlFile = new File(fulltextXmlPath);
        //logger.info("fulltext xml file is: " + fulltextXmlFile.getAbsolutePath());
        Biblio biblio = unmarshal(biblioXmlFile, Biblio.class);
        Fulltext fulltext = unmarshal(fulltextXmlFile, Fulltext.class);
        return RawDocUtils.inflateDoc(biblio.getPatentDocument(), fulltext.getPatentDocument());
    }

    /**
     * xml文件转换为java对象
     *
     * @param xml
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T unmarshal(File xml, Class<T> clazz) {
        try (FileInputStream fis = new FileInputStream(xml)) {
            T obj = unmarshal(fis, clazz, null, xml.getName());
            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从xml输入流解析java对象
     *
     * @param in
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T unmarshal(InputStream in, Class<T> type, String zipFilePath, String fileName) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Source source = new SAXSource(xmlReader, new InputSource(in));

            T obj = JAXBContext.newInstance(type)
                               .createUnmarshaller()
                               .unmarshal(source, type)
                               .getValue();
            return obj;
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(zipFilePath)) {
            copyErrorXml(in, fileName);
        }

        return null;
    }

    /**
     * 将解析失败xml复制到指定路径
     *
     * @param in
     * @param fileName
     */
    private static void copyErrorXml(InputStream in, String fileName) {
        logger.info("copy error xml in name " + fileName);

        String errorXmlPath = "/home/tqhy/data250/error/bibliographic";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        StringBuilder builder = new StringBuilder(errorXmlPath);
        builder.append(day);
        builder.append("/");
        builder.append(hour);
        builder.append("/");
        builder.append(minute);
        builder.append("/");
        String dir = builder.toString();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        File errorFile = new File(dir, fileName);

        try {
            boolean newFile = errorFile.createNewFile();
            if (newFile) {
                try (BufferedInputStream bis = new BufferedInputStream(in)) {
                    try (FileOutputStream fos = new FileOutputStream(errorFile)) {
                        int b = -1;
                        byte[] buffer = new byte[1024 * 8];
                        while ((b = bis.read(buffer)) != -1) {
                            fos.write(buffer);
                        }
                        fos.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * java对象写入xml文件
     *
     * @param obj
     * @param xml
     * @param type
     * @param <T>
     */
    public static <T> void marshal(T obj, File xml, Class<T> type) {
        try {
            JAXBContext.newInstance(type)
                       .createMarshaller()
                       .marshal(obj, xml);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * java对象写入xml文件
     *
     * @param obj     待写入的java对象
     * @param xmlPath xml文件路径
     * @param type
     * @param <T>
     */
    public static <T> void marshal(T obj, String xmlPath, Class<T> type) {
        File xmlFile = new File(xmlPath);
        marshal(obj, xmlFile, type);
    }

    public static StringBuilder parseNode(Node node) {
        StringBuilder builder = new StringBuilder();
        short nodeType = node.getNodeType();
        //logger.info("nodeType: " + nodeType);

        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                Node item = childNodes.item(i);
                StringBuilder itemBuilder = parseNode(item);
                builder.append(itemBuilder);
            }
        } else if (Node.TEXT_NODE == nodeType) {
            String textContent = node.getTextContent();
            //logger.info("item nodeType is " + nodeType + ", textContent: " + textContent);
            //移除CDATA中标签内容
            String withoutTags = textContent.replaceAll("(\\s)*([<][^>]*[>])(\\s)*", "");
            //logger.info("withoutTags is: " + withoutTags);
            String replaceAmp = withoutTags.replaceAll("&amp;", "&");
            //logger.info("replaceAmp is: " + replaceAmp);
            String escape = HtmlUtils.htmlUnescape(replaceAmp);
            //logger.info("escape is: " + escape);
            return builder.append(escape.trim());
        }
        return builder;
    }
}
