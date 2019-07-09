package com.tqhy.ip_store.models.xml.fulltext;

import com.tqhy.ip_store.models.xml.fulltext.FulltextPatentDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author Yiheng
 * @create 2018/12/13
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlRootElement(name = "Documents")
@XmlAccessorType(XmlAccessType.FIELD)
public class Fulltext implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "PatentDocument", required = true)
    private FulltextPatentDocument patentDocument;
}
