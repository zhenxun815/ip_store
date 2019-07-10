package com.tqhy.ip_store;

import com.alibaba.fastjson.JSONObject;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.repositories.RawDocRepository;
import com.tqhy.ip_store.services.RawDocService;
import com.tqhy.ip_store.utils.XmlUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {
    Logger logger = LoggerFactory.getLogger(MongoTest.class);

    @Autowired
    RawDocService rawDocService;
    @Autowired
    RawDocRepository rawDocRepository;

    @Test
    public void testFindByAppId() {
        RawDoc rawDoc = rawDocService.findByAppId("CN201480019646.2").orElse(null);
        logger.info("raw doc is {}", JSONObject.toJSONString(rawDoc));
    }

    @Test
    public void testFindById() {
        rawDocService.findById(null)
                     .ifPresent(rawDoc -> logger.info("raw doc is {}", JSONObject.toJSONString(rawDoc)));
    }

    @Test
    public void testFindByClass() {
        String section = "H";
        String mainClass = "02";
        String subClass = "A";

        Optional<List<RawDoc>> bySectionOpt = rawDocService.findBuySectionAndMainClassAndSubClass(section, mainClass, subClass);
        List<RawDoc> rawDocs = bySectionOpt.get();
        for (RawDoc rawDoc : rawDocs) {
            logger.info("get by section {} doc is {}", section, rawDoc);
        }
    }

    @Test
    public void testSave() {
        String xmlDir = "F:\\ip_data\\xml\\BIBLIOGRAPHIC_INVENTION_PUBLICATION\\CN2016103202590B";
        //String xmlName = "CN2015105122581A.XML";
        //File biblioXmlFile = new File(xmlDir, xmlName);
        RawDoc doc = XmlUtils.getRawDocFromXml(new File(xmlDir)).orElse(new RawDoc());
        logger.info("doc to save is {}", JSONObject.toJSONString(doc));
        rawDocService.save(doc)
                     .ifPresent(rawDoc -> logger.info("save doc is {}", JSONObject.toJSONString(rawDoc)));
    }
}
