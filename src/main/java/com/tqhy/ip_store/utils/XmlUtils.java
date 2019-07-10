package com.tqhy.ip_store.utils;

import com.alibaba.fastjson.JSONObject;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.models.xml.biblio.Biblio;
import com.tqhy.ip_store.models.xml.DocumentID;
import com.tqhy.ip_store.models.xml.DocumentIdInfo;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.zip.ZipFile;

/**
 * xml工具类,单例.
 *
 * @author Yiheng
 * @create 2018/12/11
 * @since 1.0.0
 */
public class XmlUtils {

    static Logger logger = LoggerFactory.getLogger(XmlUtils.class);
    static long count = 0;

    /**
     * 获取日期文件夹下所有xml文件夹数组
     *
     * @param yearDir
     * @return
     */
    public static File[] getXmlDirs(File yearDir) {
        String yearDirName = yearDir.getName();
        String parentDirPath = "CREATE/" + yearDirName + "/";
        File parentDir = new File(yearDir, parentDirPath);
        return parentDir.listFiles(File::isDirectory);
    }


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


    public static void unmarshalAllFromDirectory(String directotyPath, String type) {

        File directory = new File(directotyPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] subDirectories = directory.listFiles(File::isDirectory);
            logger.info("subDirectories length is: " + subDirectories.length);
            int processors = Runtime.getRuntime().availableProcessors();
            ForkJoinPool pool = new ForkJoinPool(processors);
            pool.invoke(new UnmarshalFilesRecursiveTask(subDirectories, type));
        }
    }

    /**
     * 解析zip文件下所有 fulltext xml
     *
     * @param zipFilePath
     */
    public static List<Fulltext> unmarshalAllFulltextFromZip(String zipFilePath) {
        logger.info("into unmarshal zip: " + zipFilePath);
        ArrayList<Fulltext> errorList = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            //String zipFileName = zipFile.getName();
            // logger.debug("zip file name is: " + zipFileName);
            System.out.print("unmarshal zip...");
            zipFile.stream()
                   .filter(zipEntry -> zipEntry.getName().matches(".+(.xml|XML)$"))
                   .forEach(zipEntry -> {
                       try (InputStream in = zipFile.getInputStream(zipEntry)) {

                           String entryName = zipEntry.getName();
                           logger.info("unmarshaling: " + entryName);
                           String[] split = entryName.split("\\\\");
                           String xmlFileName = split[split.length - 1];
                           logger.info("xml file name is: " + xmlFileName);
                           String[] split1 = xmlFileName.split("\\.");
                           String jsonFileName = split1[0];
                           Fulltext fulltext = unmarshal(in, Fulltext.class, zipFilePath, xmlFileName);
                           DocumentIdInfo documentIdInfo = fulltext.getPatentDocument()
                                                                   .getBiblioData()
                                                                   .getPublicationInfo()
                                                                   .stream()
                                                                   .filter(appInfo -> "original".equals(appInfo.getDataFormat()))
                                                                   .findFirst().orElse(null);
                           if (null == documentIdInfo) {
                               //"BIBLIOGRAPHIC_INVENTION_GRANT\\20160106\\akj.ZIP"
                               copyErrorXml(in, xmlFileName);
                           } else {
                               DocumentID docId = documentIdInfo.getDocumentID();

                               Date dateInfo = docId.getDate();
                               if (null != dateInfo) {
                                   String storePath = genStorePath("/home/tqhy/ipdata/fulltext", dateInfo);

                                   File jsonFile = new File(storePath, jsonFileName + ".json");
                                   if (jsonFile.exists()) {
                                       jsonFile.delete();
                                   }
                                   boolean newFile = jsonFile.createNewFile();
                                   if (newFile) {
                                       try (FileWriter writer = new FileWriter(jsonFile)) {
                                           writer.write(JSONObject.toJSONString(fulltext));
                                           writer.flush();
                                           count++;
                                           logger.info("count is: " + count);
                                       }
                                   }

                               }
                           }


                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   });

            System.out.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errorList;
    }

    public static List<Biblio> unmarshalAllBibliographicFromZip(String zipFilePath) {
        logger.info("into unmarshal zip: " + zipFilePath);
        ArrayList<Biblio> errorList = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            //String zipFileName = zipFile.getName();
            // logger.debug("zip file name is: " + zipFileName);
            System.out.print("unmarshal zip...");
            zipFile.stream()
                   .filter(zipEntry -> zipEntry.getName().matches(".+(.xml|XML)$"))
                   .forEach(zipEntry -> {
                       try (InputStream in = zipFile.getInputStream(zipEntry)) {

                           String entryName = zipEntry.getName();
                           logger.info("unmarshaling: " + entryName);
                           String[] split = entryName.split("\\\\");
                           String xmlFileName = split[split.length - 1];
                           logger.info("xml file name is: " + xmlFileName);
                           String[] split1 = xmlFileName.split("\\.");
                           String jsonFileName = split1[0];
                           Biblio biblio = unmarshal(in, Biblio.class, zipFilePath, xmlFileName);
                           DocumentIdInfo documentIdInfo = biblio.getPatentDocument()
                                                                 .getBiblioData()
                                                                 .getPublicationInfo()
                                                                 .stream()
                                                                 .filter(appInfo -> "original".equals(appInfo.getDataFormat()))
                                                                 .findFirst().orElse(null);
                           if (null == documentIdInfo) {
                               copyErrorXml(in, xmlFileName);
                           } else {
                               DocumentID docId = documentIdInfo.getDocumentID();

                               Date dateInfo = docId.getDate();
                               if (null != dateInfo) {
                                   String storePath = genStorePath("/home/tqhy/data250/zlx", dateInfo);

                                   File jsonFile = new File(storePath, jsonFileName + ".json");
                                   if (jsonFile.exists()) {
                                       jsonFile.delete();
                                   }
                                   boolean newFile = jsonFile.createNewFile();
                                   if (newFile) {
                                       try (FileWriter writer = new FileWriter(jsonFile)) {
                                           writer.write(JSONObject.toJSONString(biblio));
                                           writer.flush();
                                           count++;
                                           logger.info("count is: " + count);
                                       }
                                   }

                               }
                           }


                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   });

            System.out.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errorList;
    }

    /**
     * 生成json文件保存路径
     *
     * @param storeRootPath
     * @param dateInfo
     * @return
     */
    public static String genStorePath(String storeRootPath, Date dateInfo) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(dateInfo);
        String[] split = format.split("-");

        String storePath = storeRootPath + "/" + split[0] + "/" + split[1] + "/" + split[2];
        File storeDir = new File(storePath);
        if (!storeDir.exists()) {
            storeDir.mkdirs();
        }
        return storePath;

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

    /**
     * 解析文件夹下xml并行计算任务类
     *
     * @param <T>
     */
    private static class UnmarshalFilesRecursiveTask<T> extends RecursiveAction {

        private static final long serialVersionUID = 1L;
        private static final int THRESHOLD = 50;
        private File[] files;
        private String type;


        @Override
        protected void compute() {
            logger.info("files length is: " + files.length);
            if (files.length < THRESHOLD) {
                logger.info("into little..");
                if (files.length > 0) {
                    Arrays.stream(files)
                          .forEach(dir -> {
                              logger.info("dir name is: " + dir.getName());
                              File[] zipFiles = dir.listFiles(file -> file.getName().matches(".+(.zip|ZIP)$"));

                              Arrays.stream(zipFiles)
                                    .forEach(zipFile -> {
                                        logger.info("zip file name is: " + zipFile.getName());
                                        if ("fulltext".equals(type)) {
                                            unmarshalAllFulltextFromZip(zipFile.getAbsolutePath());
                                            logger.info("unmarshalAllFulltextFromZip: " + zipFile.getAbsolutePath() + " finish");
                                        } else if ("bibliographic".equals(type)) {
                                            unmarshalAllBibliographicFromZip(zipFile.getAbsolutePath());
                                            logger.info("unmarshalAllBibliographicFromZip: " + zipFile.getAbsolutePath() + " finish");
                                        }

                                    });
                          });
                }
            } else {
                int middleIndex = files.length / 2;
                File[] files1 = Arrays.copyOfRange(files, 0, middleIndex);
                File[] files2 = Arrays.copyOfRange(files, middleIndex, files.length);
                UnmarshalFilesRecursiveTask<T> task1 = new UnmarshalFilesRecursiveTask<>(files1, type);
                UnmarshalFilesRecursiveTask<T> task2 = new UnmarshalFilesRecursiveTask<>(files2, type);
                invokeAll(task1, task2);
            }
        }

        UnmarshalFilesRecursiveTask(File[] files, String type) {
            this.files = files;
            this.type = type;
        }
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
