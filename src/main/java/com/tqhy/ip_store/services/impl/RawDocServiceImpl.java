package com.tqhy.ip_store.services.impl;

import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.repositories.RawDocRepository;
import com.tqhy.ip_store.services.RawDocService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
@Service
public class RawDocServiceImpl implements RawDocService {

    @Autowired
    RawDocRepository repository;

    @Override
    public Optional<List<RawDoc>> findAll() {
        return null;
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
        return null;
    }

    @Override
    public Optional<List<RawDoc>> findBySection(String section) {
        List<RawDoc> rawDocs = repository.findBySection(section);
        if (rawDocs.size() > 0) {
            return Optional.of(rawDocs);
        }
        return Optional.of(new ArrayList<>());
    }

    @Override
    public Optional<List<RawDoc>> findBySectionAndMainClass(String section, String mainClass) {
        List<RawDoc> rawDocs = repository.findBySectionAndMainClass(section, mainClass);
        if (rawDocs.size() > 0) {
            return Optional.of(rawDocs);
        }
        return Optional.of(new ArrayList<>());
    }

    @Override
    public Optional<List<RawDoc>> findBuySectionAndMainClassAndSubClass(String section, String mainClass, String subClass) {
        List<RawDoc> rawDocs = repository.findBySectionAndMainClassAndSubClass(section, mainClass, subClass);
        if (rawDocs.size() > 0) {
            return Optional.of(rawDocs);
        }
        return Optional.of(new ArrayList<>());
    }

    @Override
    public Optional<RawDoc> save(@NonNull RawDoc rawDoc) {
        return Optional.ofNullable(repository.save(rawDoc));
    }

    @Override
    public Optional<List<RawDoc>> save(List<RawDoc> rawDocs) {
        repository.save(rawDocs);
        return null;
    }

    @Override
    public void deleteByAppId(String appId) {

    }

    @Override
    public void deleteByPubId(String pubId) {

    }

    @Override
    public void deleteById(String id) {

    }
}
