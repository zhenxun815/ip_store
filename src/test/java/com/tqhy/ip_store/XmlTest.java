package com.tqhy.ip_store;

import com.alibaba.fastjson.JSONObject;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.models.xml.biblio.Biblio;
import com.tqhy.ip_store.utils.FileUtils;
import com.tqhy.ip_store.utils.XmlUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
public class XmlTest {

    Logger logger = LoggerFactory.getLogger(XmlTest.class);

    @Test
    public void testParseXml() {
        String xmlDir = "F:\\ip_data\\xml\\BIBLIOGRAPHIC_INVENTION_PUBLICATION\\CN2011102246839A";
        String xmlName = "CN2011102246839A.XML";
        File biblioXmlFile = new File(xmlDir, xmlName);
        Biblio biblio = XmlUtils.unmarshal(biblioXmlFile, Biblio.class);
        //logger.info("biblio is {}", XmlUtils.parseNode(biblio.getPatentDocument().getBiblioData().getTitle()));
        String title = biblio.getPatentDocument().getBiblioData().getTitle().toString();
        logger.info("title is {}", title);

        RawDoc rawDoc = XmlUtils.getRawDocFromXml(new File(xmlDir)).orElse(null);
        rawDoc.set_id(new ObjectId());
        logger.info("raw doc is: {}", JSONObject.toJSONString(rawDoc));
    }

    @Test
    public void testHtmlSanitizer() {
        String text = "从POF<Sub>3</Sub>或PF<Sub>5</Sub>制造LiPO<Sub>2</Sub>F<Sub>2</Sub>";
        PolicyFactory policyFactory = new HtmlPolicyBuilder().toFactory();
        String sanitize = policyFactory.sanitize(text);
        logger.info("text is {}", sanitize);
    }

}
