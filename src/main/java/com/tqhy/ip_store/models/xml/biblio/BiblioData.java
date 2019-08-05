package com.tqhy.ip_store.models.xml.biblio;

import com.tqhy.ip_store.models.xml.DocumentIdInfo;
import com.tqhy.ip_store.models.xml.TitleHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

/**
 * @author Yiheng
 * @create 2018/12/7
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BibliographicData")
public class BiblioData implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "PublicationInfo")
    private List<DocumentIdInfo> publicationInfo;

    @XmlElement(name = "ApplicationInfo")
    private List<DocumentIdInfo> applicationInfo;

/*
    @XmlElement(name = "PriorityClaim")
    @XmlElementWrapper(name = "PriorityClaimDetails")
    private List<DocumentIdInfo> priorityClaimDetails;
*/

    @XmlElement(name = "ClassificationIPCR")
    @XmlElementWrapper(name = "ClassificationsIPCR")
    private List<ClassificationIPCR> classificationIPCRs;

    @XmlElement(name = "ClassificationIPC")
    private ClassificationIPC classificationIPC;

    @XmlAnyElement(value = TitleHandler.class)
    private String title;
}
