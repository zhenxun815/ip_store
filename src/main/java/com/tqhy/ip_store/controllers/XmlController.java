package com.tqhy.ip_store.controllers;


import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.services.RawDocService;
import com.tqhy.ip_store.tasks.ParseXmlInDirTask;
import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * xml相关操作接Controller
 *
 * @author Yiheng
 * @create 3/18/2019
 * @since 1.0.0
 */
@RestController
@RequestMapping("/xml")
public class XmlController {

    Logger logger = LoggerFactory.getLogger(XmlController.class);
    @Autowired
    RawDocService rawDocService;

    @PostMapping("/parse/save")
    public void parseXmlClaim(@RequestParam("baseDir") String baseDirPath) {
        logger.info("baseDirPath is: {}", baseDirPath);

        HashMap<String, String> map = new HashMap<>();
        /*map.put("CN_BIBLIOGRAPHIC_ZH/BIBLIOGRAPHIC_INVENTION_GRANT", "CN_FULLTEXT_ZH/FULLTEXT_INVENTION_GRANT");
        map.put("CN_BIBLIOGRAPHIC_ZH/BIBLIOGRAPHIC_INVENTION_PUBLICATION", "CN_FULLTEXT_ZH/FULLTEXT_INVENTION_PUBLICATION");
        map.put("CN_BIBLIOGRAPHIC_ZH/BIBLIOGRAPHIC_UTILITY_MODEL", "CN_FULLTEXT_ZH/FULLTEXT_UTILITY_MODEL");*/

        map.put("BIBLIOGRAPHIC_INVENTION_GRANT", "FULLTEXT_INVENTION_GRANT");
        map.put("BIBLIOGRAPHIC_INVENTION_PUBLICATION", "FULLTEXT_INVENTION_PUBLICATION");
        map.put("BIBLIOGRAPHIC_UTILITY_MODEL", "FULLTEXT_UTILITY_MODEL");

        AtomicInteger taskCount = new AtomicInteger(0);

        logger.info("total task count is: " + taskCount.get());

        map.forEach((biblioBasePath, fulltextBasePath) -> {
            File workBiblioDir = new File(baseDirPath, biblioBasePath);
            if (workBiblioDir.exists()) {
                File[] yearDirs = workBiblioDir.listFiles(File::isDirectory);

                for (File yearDir : yearDirs) {

                    Flowable<RawDoc> flowable = Flowable.create(ParseXmlInDirTask.with(yearDir), BackpressureStrategy.BUFFER);

                    flowable.subscribeOn(Schedulers.newThread())
                            .observeOn(Schedulers.newThread())
                            .subscribe(new FlowableSubscriber<RawDoc>() {
                                List<RawDoc> rawDocs = new ArrayList<>();

                                @Override
                                public void onSubscribe(Subscription subscription) {
                                    logger.info("request 1 subscription");
                                    subscription.request(1);
                                }

                                @Override
                                public void onNext(RawDoc rawDoc) {
                                    logger.info("raw doc on next to save is {}", rawDoc.getAppId());
                                    rawDocs.add(rawDoc);
                                    logger.info("on next size {}", rawDocs.size());
                                    if (rawDocs.size() == 100) {
                                        List<RawDoc> saveDocs = rawDocService.save(rawDocs).orElse(new ArrayList<>());
                                        logger.info("on next save ... {}", saveDocs.size());
                                        rawDocs.clear();
                                    } else {
                                        logger.info("on next else");
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    logger.error("save raw doc subscription get error", throwable);
                                }

                                @Override
                                public void onComplete() {
                                    logger.info("on complete...");
                                    if (rawDocs.size() > 0) {
                                        List<RawDoc> saveDocs = rawDocService.save(rawDocs).orElse(new ArrayList<>());
                                        logger.info("on complete save ... {}", saveDocs.size());
                                        rawDocs.clear();
                                    }
                                }
                            });
                }
            }
        });
        logger.info("all parse xml task complete...");

    }


}
