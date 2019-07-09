package com.tqhy.ip_store.models.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DocumentID")
public class DocumentID implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "Country")
    private String country;

    @XmlElement(name = "Number")
    private String number;

    @XmlElement(name = "Date")
    @XmlJavaTypeAdapter(XmlAdapterDate.class)
    private Date date;

    @XmlElement(name = "Kind")
    private String kind;

    @XmlElement(name = "Name")
    private String name;
}
