package com.tqhy.ip_store.services.impl;

import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.repositories.RawDocRepository;
import com.tqhy.ip_store.services.RawDocService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
@Service
public class RawDocServiceImpl implements RawDocService {

    Logger logger = LoggerFactory.getLogger(RawDocServiceImpl.class);

    @Autowired
    RawDocRepository repository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<RawDoc> findAll(PageRequest pageRequest) {
        Query query = new Query().with(pageRequest);
        Page<RawDoc> page = findByQuery(pageRequest, query);
        return page;
    }

    @Override
    public Optional<RawDoc> findById(String id) {
        RawDoc rawDoc = repository.findBy_id(new ObjectId(id));
        return Optional.ofNullable(rawDoc);
    }

    @Override
    public Optional<RawDoc> findByAppId(String appId) {
        List<RawDoc> rawDocs = repository.findByAppId(appId);
        if (rawDocs.size() > 0) {
            return Optional.of(rawDocs.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<RawDoc> findByPubId(String pubId) {
        List<RawDoc> rawDocs = repository.findByPubId(pubId);
        if (rawDocs.size() > 0) {
            return Optional.of(rawDocs.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Page<RawDoc> findBySection(String section,
                                      PageRequest pageRequest) {
        Query query = new Query().with(pageRequest)
                                 .addCriteria(Criteria.where("section").is(section.toUpperCase()));
        Page<RawDoc> page = findByQuery(pageRequest, query);
        return page;
    }


    @Override
    public Page<RawDoc> findBySectionAndMainClass(String section,
                                                  String mainClass,
                                                  PageRequest pageRequest) {
        Query query = new Query().with(pageRequest)
                                 .addCriteria(Criteria.where("section").is(section.toUpperCase()))
                                 .addCriteria(Criteria.where("mainClass").is(mainClass));
        Page<RawDoc> page = findByQuery(pageRequest, query);
        return page;
    }

    @Override
    public Page<RawDoc> findBySectionAndMainClassAndSubClass(String section,
                                                             String mainClass,
                                                             String subClass,
                                                             PageRequest pageRequest) {
        Query query = new Query().with(pageRequest)
                                 .addCriteria(Criteria.where("section").is(section.toUpperCase()))
                                 .addCriteria(Criteria.where("mainClass").is(mainClass))
                                 .addCriteria(Criteria.where("subClass").is(subClass.toUpperCase()));
        Page<RawDoc> page = findByQuery(pageRequest, query);
        return page;
    }

    @Override
    public Optional<RawDoc> save(RawDoc rawDoc) {
        return Optional.ofNullable(repository.save(rawDoc));
    }

    @Override
    public Optional<List<RawDoc>> save(List<RawDoc> rawDocs) {
        logger.info("into save all with size {}", rawDocs.size());
        List<RawDoc> save = repository.saveAll(rawDocs);
        return Optional.ofNullable(save);
    }

    @Override
    public void deleteByAppId(String appId) {
        repository.deleteRawDocsByAppId(appId);
    }

    @Override
    public void deleteByPubId(String pubId) {
        repository.deleteRawDocByPubId(pubId);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteBy_id(new ObjectId(id));
    }


    private Page<RawDoc> findByQuery(PageRequest pageRequest, Query query) {
        List<RawDoc> rawDocs = mongoTemplate.find(query, RawDoc.class, "raw");
        return PageableExecutionUtils.getPage(rawDocs,
                                              pageRequest,
                                              () -> mongoTemplate.count(query, RawDoc.class));
    }
}
