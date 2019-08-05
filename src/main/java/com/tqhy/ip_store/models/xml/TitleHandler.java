package com.tqhy.ip_store.models.xml;

import com.tqhy.ip_store.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Yiheng
 * @create 8/5/2019
 * @since 1.0.0
 */
public class TitleHandler implements DomHandler<String, StreamResult> {

    Logger logger = LoggerFactory.getLogger(TitleHandler.class);
    private static final String BIO_START_TAG = "<Title lang=\"zh\" format=\"original\">";
    private static final String BIO_END_TAG = "</Title>";

    private StringWriter xmlWriter = new StringWriter();

    @Override
    public StreamResult createUnmarshaller(ValidationEventHandler errorHandler) {
        return new StreamResult(xmlWriter);
    }

    @Override
    public String getElement(StreamResult rt) {
        String xml = rt.getWriter().toString();
        //logger.info("xml is {}", xml);
        if (xml.indexOf(BIO_START_TAG) > 0) {
            int beginIndex = xml.indexOf(BIO_START_TAG) + BIO_START_TAG.length();
            int endIndex = xml.indexOf(BIO_END_TAG);
            return XmlUtils.removeTags(xml.substring(beginIndex, endIndex));
        }
        return null;
    }

    @Override
    public Source marshal(String n, ValidationEventHandler errorHandler) {
        try {
            String xml = BIO_START_TAG + n.trim() + BIO_END_TAG;
            StringReader xmlReader = new StringReader(xml);
            return new StreamSource(xmlReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
