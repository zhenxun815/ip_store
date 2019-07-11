package com.tqhy.ip_store.models.xml.biblio;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Yiheng
 * @create 7/11/2019
 * @since 1.0.0
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassificationIPC {

    @XmlElement(name = "MainClassification")
    private ClassificationIPCR mainClassification;
}
