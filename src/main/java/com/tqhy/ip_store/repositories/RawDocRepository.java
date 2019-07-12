package com.tqhy.ip_store.repositories;

import com.tqhy.ip_store.models.mongo.RawDoc;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
public interface RawDocRepository extends MongoRepository<RawDoc, String> {

    RawDoc findBy_id(ObjectId _id);

    List<RawDoc> findByPubId(String pubId);

    List<RawDoc> findByAppId(String appId);

    List<RawDoc> findBySection(String section);

    List<RawDoc> findBySectionAndMainClass(String section, String mainClass);

    List<RawDoc> findBySectionAndMainClassAndSubClass(String section, String mainClass, String subClass);

    RawDoc save(RawDoc rawDoc);

    <S extends RawDoc> List<S> saveAll(Iterable<S> entities);

    void deleteBy_id(ObjectId _id);

    void deleteRawDocsByAppId(String appId);

    void deleteRawDocByPubId(String pubId);

}
