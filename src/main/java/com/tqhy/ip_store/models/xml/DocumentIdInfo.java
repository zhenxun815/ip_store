package com.tqhy.ip_store.models.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentIdInfo {

    @XmlAttribute(name = "dataFormat")
    private String dataFormat;

    @XmlAttribute(name = "kind")
    private String kind;

    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "firstPriority")
    private boolean firstPriority;

    @XmlElement(name = "DocumentID")
    private DocumentID documentID;

    /*@XmlAttribute(name = "sequence")
      private Integer sequence;*/
}
