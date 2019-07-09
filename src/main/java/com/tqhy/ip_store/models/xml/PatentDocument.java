package com.tqhy.ip_store.models.xml;

import com.tqhy.ip_store.models.xml.biblio.Abs;
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
 * @create 7/9/2019
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class PatentDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "BibliographicData")
    private BiblioData biblioData;
}
