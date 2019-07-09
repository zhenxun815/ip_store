package com.tqhy.ip_store.models.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlValue;
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
public class Paragraph implements Serializable {
    private static final long serialVersionUID = 1L;
/*
    @XmlAttribute(name = "appId")
    private String appId;

    @XmlAttribute(name = "number")
    private String number;*/

    @XmlValue
    private String paragraph;

}
