package com.tqhy.ip_store.models.xml.biblio;

import com.tqhy.ip_store.models.xml.PatentDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * 主专利
 *
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class BiblioPatentDocument extends PatentDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Abstract")
    private Abs abs;
}
