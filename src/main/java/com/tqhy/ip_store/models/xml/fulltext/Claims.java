package com.tqhy.ip_store.models.xml.fulltext;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;
import java.util.List;

/**
 * @author Yiheng
 * @create 2018/12/9
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Claims implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private String lang;

    @XmlAttribute
    private String claimType;

    @XmlAttribute
    private String format;


    @XmlAnyElement
    private List<Node> claimTexts;
}
