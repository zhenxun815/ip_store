package com.tqhy.ip_store.models.xml.biblio;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlType(name = "Documents")
@XmlAccessorType(XmlAccessType.FIELD)
public class Biblio implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "PatentDocument", required = true)
    private BiblioPatentDocument patentDocument;

}
