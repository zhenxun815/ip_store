package com.tqhy.ip_store.models.xml.biblio;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Yiheng
 * @create 2018/12/8
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Title")
public class Title implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "lang")
    private String lang;

    @XmlAttribute(name = "format")
    private String format;

    @XmlValue
    private String title;
}
