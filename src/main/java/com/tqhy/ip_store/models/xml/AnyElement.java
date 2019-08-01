package com.tqhy.ip_store.models.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Yiheng
 * @create 2018/12/10
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class AnyElement implements Serializable {
    private static final long serialVersionUID = 1L;
/*
    @XmlAttribute(name = "appId")
    private String appId;

    @XmlAttribute(name = "number")
    private String number;*/

    @XmlAnyElement
    private List<Node> paragraph;

}
