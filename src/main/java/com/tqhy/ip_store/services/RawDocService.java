package com.tqhy.ip_store.services;

import com.tqhy.ip_store.models.mongo.RawDoc;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
public interface RawDocService {

    Optional<List<RawDoc>> findAll();

    Optional<RawDoc> findById(String id);

    Optional<RawDoc> findByAppId(String appId);

    Optional<RawDoc> findByPubId(String pubId);

    Optional<List<RawDoc>> findBySection(String section);

    Optional<List<RawDoc>> findBySectionAndMainClass(String section, String mainClass);

    Optional<List<RawDoc>> findBuySectionAndMainClassAndSubClass(String section, String mainClass, String subClass);

    Optional<RawDoc> save(RawDoc rawDoc);

    Optional<List<RawDoc>> save(List<RawDoc> rawDocs);

    void deleteByAppId(String appId);

    void deleteByPubId(String pubId);

    void deleteById(String id);
}
