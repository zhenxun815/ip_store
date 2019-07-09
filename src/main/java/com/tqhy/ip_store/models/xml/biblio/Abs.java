package com.tqhy.ip_store.models.xml.biblio;

import com.tqhy.ip_store.models.xml.Paragraph;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

    @XmlElement(name = "Paragraph")
    private Paragraph paragraph;

  /*  @XmlElementWrapper(name = "AbstractFigure")
    @XmlElement(name = "Figure")
    private List<Figure> figures;*/
}
