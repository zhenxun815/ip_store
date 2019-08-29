package com.tqhy.ip_store;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tqhy.ip_store.configs.Constants;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.repositories.RawDocRepository;
import com.tqhy.ip_store.services.RawDocService;
import com.tqhy.ip_store.utils.FileUtils;
import com.tqhy.ip_store.utils.StringUtils;
import com.tqhy.ip_store.utils.XmlUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Page<RawDoc> rawDocPage =
                rawDocService.findBySectionAndMainClassAndSubClass(section, mainClass, subClass,
                                                                   PageRequest.of(0, 300));
        List<RawDoc> rawDocs = rawDocPage.getContent();
        for (RawDoc rawDoc : rawDocs) {
            logger.info("get by section {} doc is {}", section, rawDoc);
        }
    }

    @Test
    public void testSave() {
        String xmlDir = "F:\\ip_data\\xml\\BIBLIOGRAPHIC_INVENTION_PUBLICATION\\CN2016103202590B";
        RawDoc doc = XmlUtils.getRawDocFromXml(new File(xmlDir)).orElse(new RawDoc());
        logger.info("doc to save is {}", JSONObject.toJSONString(doc));
        rawDocService.save(doc)
                     .ifPresent(rawDoc -> logger.info("save doc is {}", JSONObject.toJSONString(rawDoc)));
    }

    @Test
    public void testUpdate() {
        String xmlDir = "F:\\ip_data\\xml\\BIBLIOGRAPHIC_INVENTION_PUBLICATION\\CN2016102721435B";
        RawDoc doc = XmlUtils.getRawDocFromXml(new File(xmlDir)).orElse(new RawDoc());
        boolean update = rawDocService.update(doc);
        logger.info("update {}: {}", doc.getPubId(), update);
        doc.set_id(new ObjectId());
        logger.info("doc to save is {}", JSONObject.toJSONString(doc));
    }

    @Test
    public void testCountClassification() {
        Pattern compile = Pattern.compile("(?<section>[A-Z])(?<mainClass>[0-9]{2})(?<subClass>[A-Z])");

        String clfFilePath = "F:\\ip_data\\ip_search\\classification\\clf.txt";
        String clfCountFilePath = "F:\\ip_data\\ip_search\\classification\\clf_count.txt";
        File clfFile = new File(clfFilePath);
        File clfCountFile = new File(clfCountFilePath);
        AtomicInteger lt25Count = new AtomicInteger(0);
        AtomicInteger lt2500Count = new AtomicInteger(0);
        if (FileUtils.createNewFile(clfCountFile)) {
            FileUtils.readLine(clfFile, line -> {
                Matcher matcher = compile.matcher(line);
                if (matcher.matches()) {
                    String section = matcher.group("section");
                    String mainClass = matcher.group("mainClass");
                    String subClass = matcher.group("subClass");
                    long count = rawDocService.countBySectionAndMainClassAndSubClass(section, mainClass, subClass);
                    if (count < 25) {
                        lt25Count.incrementAndGet();
                    }
                    if (count < 2500) {
                        lt2500Count.incrementAndGet();
                    }

                    logger.info("clf is {}, count is {}", line, count);
                    FileUtils.appendFile(clfCountFile,
                                         line,
                                         builder -> builder.append(":").append(count).append(Constants.NEW_LINE),
                                         true);
                } else {
                    logger.info("not match {}", line);
                }
            });
        } else {
            logger.info("create clf count file [{}] failed", clfCountFilePath);
        }

        logger.info("lt25count is {},lt2500count is {}", lt25Count.get(), lt2500Count.get());
    }

    @Test
    public void testWrite2Local() {
        Pattern compile = Pattern.compile("(?<section>[A-Z])(?<mainClass>[0-9]{2})(?<subClass>[A-Z])");
        String clfCountFilePath = "F:\\ip_data\\ip_search\\classification\\clf_count.txt";
        String clfJsonDirPath = "E:\\ip_data\\clfs\\seged\\limit2500";
        File clfCountFile = new File(clfCountFilePath);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        FileUtils.readLine(clfCountFile, ":", split -> {
            String clfStr = split[0];
            int count = Integer.parseInt(split[1]);
            if (count < 25) {
                return;
            }

            Matcher matcher = compile.matcher(clfStr);
            if (matcher.matches()) {
                String section = matcher.group("section");
                String mainClass = matcher.group("mainClass");
                String subClass = matcher.group("subClass");
                logger.info("clf is {}, count is {}", clfStr, count);

                Page<RawDoc> rawDocPage =
                        rawDocService.findBySectionAndMainClassAndSubClass(section,
                                                                           mainClass,
                                                                           subClass,
                                                                           PageRequest.of(0, 2500));

                pool.submit(() -> {
                    List<RawDoc> rawDocs = rawDocPage.getContent();
                    String clfJsonFileName = StringUtils.join(clfStr, "_", Integer.toString(rawDocs.size()), ".txt");
                    File clfJsonFile = new File(clfJsonDirPath, clfJsonFileName);
                    rawDocs.forEach(rawDoc -> FileUtils.appendFile(clfJsonFile,
                                                                   JSON.toJSONString(rawDoc),
                                                                   builder -> builder.append(Constants.NEW_LINE),
                                                                   true));
                });

            } else {
                logger.info("not match {}", clfStr);
            }
        });
    }

}
