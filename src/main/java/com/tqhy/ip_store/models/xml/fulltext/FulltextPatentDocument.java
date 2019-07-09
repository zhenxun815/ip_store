package com.tqhy.ip_store.models.xml.fulltext;

import com.tqhy.ip_store.models.xml.PatentDocument;
import com.tqhy.ip_store.models.xml.biblio.BiblioData;
import com.tqhy.ip_store.models.xml.fulltext.Claims;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * @author Yiheng
 * @create 2018/12/13
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class FulltextPatentDocument extends PatentDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Claims")
    private Claims claims;
/*
    @XmlElement(name = "Description")
    private Description description;

    @XmlAttribute(name = "sequence")
    private Integer sequence;

    @XmlAttribute(name = "status")
    private String status;

    @XmlElementWrapper(name = "Drawings")
    @XmlElement(name = "Figure")
    private List<Figure> drawings;
*/
}
