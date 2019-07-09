package com.tqhy.ip_store.models.mongo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Yiheng
 * @create 7/9/2019
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@Document(collection = "raw")
public class RawDoc {

    @Id
    private ObjectId _id;

    private String pubId;

    private String appId;

    private String title;

    private String abs;

    private String claim;

    private Long applicationDate;

    private Long publicationDate;

    private String section;

    private String mainClass;

    private String subClass;

    public String get_id() {
        return _id.toString();
    }
}
