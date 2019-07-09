package com.tqhy.ip_store.models.xml.biblio;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Yiheng
 * @create 2019/4/28
 * @since 1.0.0
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassificationIPCR {

    @XmlAttribute(name = "sequence")
    private String sequence;

    @XmlElement(name = "IPCVersion")
    private String ipcVersion;

    @XmlElement(name = "Section")
    private String section;

    @XmlElement(name = "MainClass")
    private String mainClass;

    @XmlElement(name = "SubClass")
    private String subClass;

    @XmlElement(name = "MainGroup")
    private String mainGroup;

    @XmlElement(name = "SubGroup")
    private String subGroup;

    @XmlElement(name = "GeneratingOffice")
    private String generatingOffice;

    @XmlElement(name = "ClassificationDataSource")
    private String classificationDataSource;

    @XmlElement(name = "Text")
    private String text;

    @Override
    public String toString() {
        StringBuilder ipcrBuilder = new StringBuilder(this.getSection());
        String ipcrClassificationStr = ipcrBuilder.append(this.getMainClass())
                                                  .append(this.getSubClass())
                                                  .toString();
        return ipcrClassificationStr;
    }
}
