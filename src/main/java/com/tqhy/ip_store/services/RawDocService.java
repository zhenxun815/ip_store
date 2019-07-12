package com.tqhy.ip_store.services;

import com.tqhy.ip_store.models.mongo.RawDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
public interface RawDocService {

    Page<RawDoc> findAll(PageRequest pageRequest);

    Optional<RawDoc> findById(String id);

    Optional<RawDoc> findByAppId(String appId);

    Optional<RawDoc> findByPubId(String pubId);

    Page<RawDoc> findBySection(String section, PageRequest pageRequest);

    Page<RawDoc> findBySectionAndMainClass(String section, String mainClass, PageRequest pageRequest);

    Page<RawDoc> findBySectionAndMainClassAndSubClass(String section, String mainClass,
                                                      String subClass, PageRequest pageRequest);

    Optional<RawDoc> save(RawDoc rawDoc);

    Optional<List<RawDoc>> save(List<RawDoc> rawDocs);

    void deleteByAppId(String appId);

    void deleteByPubId(String pubId);

    void deleteById(String id);
}
