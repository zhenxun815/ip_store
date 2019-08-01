package com.tqhy.ip_store.models.xml.biblio;

import com.tqhy.ip_store.models.xml.AnyElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author Yiheng
 * @create 2018/12/9
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Abstract")
public class Abs implements Serializable {
    private static final long serialVersionUID = 1L;

   /* @XmlAttribute(name = "format")
    private String format;

    @XmlAttribute(name = "lang")
    private String lang;*/

    @XmlAnyElement
    private Node paragraph;

    @XmlElement(name = "AbstractFigure")
    private AnyElement figures;
}
