package com.tqhy.ip_store.controllers;

import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.services.RawDocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
@RestController
@RequestMapping(path = "/raw-docs")
public class RawDocController {

    @Autowired
    RawDocService rawDocService;

    Logger logger = LoggerFactory.getLogger(RawDocController.class);

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<RawDoc> getAll(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        logger.info("request get all with page {}, size {}", pageNum, pageSize);
        return rawDocService.findAll(PageRequest.of(pageNum, pageSize));
    }

    @GetMapping({"/class/{section}", "/class/{section}/{mainClass}", "/class/{section}/{mainClass}/{subClass}"})
    @ResponseStatus(HttpStatus.OK)
    public Page<RawDoc> getByClassification(@PathVariable(name = "section", required = false) String section,
                                            @PathVariable(name = "mainClass", required = false) String mainClass,
                                            @PathVariable(name = "subClass", required = false) String subClass,
                                            @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        logger.info("request get by classification with {}/{}/{}, page {}, size {}",
                    section, mainClass, subClass, pageNum, pageSize);
        if (StringUtils.isEmpty(section)) {
            return rawDocService.findAll(PageRequest.of(pageNum, pageSize));
        }
        if (StringUtils.isEmpty(mainClass)) {
            return rawDocService.findBySection(section, PageRequest.of(pageNum, pageSize));
        }
        if (StringUtils.isEmpty(subClass)) {
            return rawDocService.findBySectionAndMainClass(section, mainClass, PageRequest.of(pageNum, pageSize));
        }
        return rawDocService.findBySectionAndMainClassAndSubClass(section, mainClass, subClass, PageRequest.of(pageNum, pageSize));
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RawDoc getById(@PathVariable(name = "id") String id) {
        logger.info("request get by id with {}", id);
        if (id.toLowerCase().startsWith("cn")) {
            if (id.contains(".")) {
                return rawDocService.findByAppId(id).orElse(null);
            }
            return rawDocService.findByPubId(id).orElse(null);
        }
        return rawDocService.findById(id).orElse(null);
    }

}
