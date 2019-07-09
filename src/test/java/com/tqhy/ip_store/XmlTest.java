package com.tqhy.ip_store;

import com.alibaba.fastjson.JSONObject;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.models.xml.biblio.Biblio;
import com.tqhy.ip_store.utils.XmlUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
public class XmlTest {

    Logger logger = LoggerFactory.getLogger(XmlTest.class);

    @Test
    public void testParseXml() {
        String xmlDir = "F:\\ip_data\\xml\\BIBLIOGRAPHIC_INVENTION_PUBLICATION\\CN2015105122581A";
        String xmlName = "CN2015105122581A.XML";
        /*
        File biblioXmlFile = new File(xmlDir, xmlName);
        Biblio biblio = XmlUtils.unmarshal(biblioXmlFile, Biblio.class);
        logger.info("biblio is {}", biblio);
        */
        RawDoc rawDoc = XmlUtils.getRawDocFromXml(new File(xmlDir));
        logger.info("raw doc is: {}", JSONObject.toJSONString(rawDoc));
    }
}
