package com.tqhy.ip_store.controllers;


import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.services.RawDocService;
import com.tqhy.ip_store.tasks.StoreRawDocsPublisher;
import com.tqhy.ip_store.tasks.StoreRawDocsSubscriber;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

        /*map.put("CN_BIBLIOGRAPHIC_ZH/BIBLIOGRAPHIC_INVENTION_GRANT", "CN_FULLTEXT_ZH/FULLTEXT_INVENTION_GRANT");
        map.put("CN_BIBLIOGRAPHIC_ZH/BIBLIOGRAPHIC_INVENTION_PUBLICATION", "CN_FULLTEXT_ZH/FULLTEXT_INVENTION_PUBLICATION");
        map.put("CN_BIBLIOGRAPHIC_ZH/BIBLIOGRAPHIC_UTILITY_MODEL", "CN_FULLTEXT_ZH/FULLTEXT_UTILITY_MODEL");*/

        /*map.put("BIBLIOGRAPHIC_INVENTION_GRANT", "FULLTEXT_INVENTION_GRANT");
        map.put("BIBLIOGRAPHIC_INVENTION_PUBLICATION", "FULLTEXT_INVENTION_PUBLICATION");
        map.put("BIBLIOGRAPHIC_UTILITY_MODEL", "FULLTEXT_UTILITY_MODEL");*/

        String[] biblioBasePathArr =
                {"BIBLIOGRAPHIC_INVENTION_GRANT", "BIBLIOGRAPHIC_INVENTION_PUBLICATION", "BIBLIOGRAPHIC_UTILITY_MODEL"};

        Flowable.create(StoreRawDocsPublisher.with(baseDirPath, biblioBasePathArr), BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new StoreRawDocsSubscriber() {
                    @Override
                    public List<RawDoc> save(List<RawDoc> rawDocs) {
                        return rawDocService.save(rawDocs).orElse(new ArrayList<>());
                    }
                });


    }


}
