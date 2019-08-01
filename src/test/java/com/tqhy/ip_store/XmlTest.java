package com.tqhy.ip_store;

import com.alibaba.fastjson.JSONObject;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.models.xml.biblio.Biblio;
import com.tqhy.ip_store.utils.XmlUtils;
import org.junit.Test;
import org.owasp.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
public class XmlTest {

    Logger logger = LoggerFactory.getLogger(XmlTest.class);

    @Test
    public void testParseXml() {
        String xmlDir = "F:\\ip_data\\xml\\BIBLIOGRAPHIC_INVENTION_PUBLICATION\\CN2016102721435B";
        String xmlName = "CN2016102721435B.XML";
        File biblioXmlFile = new File(xmlDir, xmlName);
        Biblio biblio = XmlUtils.unmarshal(biblioXmlFile, Biblio.class);
        logger.info("biblio is {}", XmlUtils.parseNode(biblio.getPatentDocument().getAbs().getParagraph()));
        //RawDoc rawDoc = XmlUtils.getRawDocFromXml(new File(xmlDir)).orElse(null);
        //logger.info("raw doc is: {}", JSONObject.toJSONString(rawDoc));
    }

    @Test
    public void testHtmlSanitizer() {
        String text = "一种均<![CDATA[开始，由 ]]>相水热法制备花状Cu<sub>2</sub>V<sub>2</sub>O<sub>7</sub>材料的方法及制备的Cu<sub>2</sub>V<sub>2</sub>O<sub>7</sub>材料";
        PolicyFactory policyFactory = new HtmlPolicyBuilder().toFactory();
        String sanitize = policyFactory.sanitize(text);
        logger.info("text is {}", sanitize);
    }
}
