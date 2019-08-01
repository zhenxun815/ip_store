package com.tqhy.ip_store.utils;

import com.tqhy.ip_store.configs.Constants;
import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.models.xml.*;
import com.tqhy.ip_store.models.xml.biblio.*;
import com.tqhy.ip_store.models.xml.fulltext.Claims;
import com.tqhy.ip_store.models.xml.fulltext.Fulltext;
import com.tqhy.ip_store.models.xml.fulltext.FulltextPatentDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.*;

/**
 * @author Yiheng
 * @create 2/20/2019
 * @since 1.0.0
 */
public class RawDocUtils {

    static Logger logger = LoggerFactory.getLogger(RawDocUtils.class);

    /**
     * 填充著录项目信息到文档
     *
     * @param originalData
     * @return
     */
    public static Optional<RawDoc> inflateDoc(BiblioPatentDocument originalData, FulltextPatentDocument fulltextData) {
        RawDoc rawDoc = new RawDoc();
        if (null == originalData || null == fulltextData) {
            return Optional.empty();
        }
        BiblioData biblioData = originalData.getBiblioData();
        String title = generateTitle(biblioData.getTitle());
        rawDoc.setTitle(title);

        Abs abs = originalData.getAbs();
        String paragraph = generateAbs(abs);
        rawDoc.setAbs(paragraph);

        Map<String, String> pubInfoMap = generateDocIdInfo(biblioData.getPublicationInfo());
        String pubId = pubInfoMap.get("id");
        String date = pubInfoMap.get("date");
        logger.info("inflate doc {}", pubId);
        if (StringUtils.isEmpty(pubId) && StringUtils.isEmpty(date)) {
            return Optional.empty();
        }
        rawDoc.setPubId(pubId);
        rawDoc.setPublicationDate(Long.parseLong(date));

        Map<String, String> appInfoMap = generateDocIdInfo(biblioData.getApplicationInfo());
        String appId = appInfoMap.get("id");
        String appDate = appInfoMap.get("date");
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appDate)) {
            return Optional.empty();
        }
        rawDoc.setAppId(appId);
        rawDoc.setApplicationDate(Long.parseLong(appDate));
        Optional<ClassificationIPCR> primaryClassificationOpt = getPrimaryClassification(biblioData);
        if (primaryClassificationOpt.isPresent()) {
            ClassificationIPCR primaryClassification = primaryClassificationOpt.get();
            rawDoc.setSection(primaryClassification.getSection());
            rawDoc.setMainClass(primaryClassification.getMainClass());
            rawDoc.setSubClass(primaryClassification.getSubClass());
        } else {
            return Optional.empty();
        }

        String claim = generateClaim(fulltextData.getClaims());
        rawDoc.setClaim(claim);
        return Optional.ofNullable(rawDoc);
    }

    private static Optional<ClassificationIPCR> getPrimaryClassification(BiblioData biblioData) {
        ClassificationIPC classificationIPC = biblioData.getClassificationIPC();
        return null == classificationIPC ? getPrimaryClassificationIPCR(biblioData) :
                Optional.ofNullable(classificationIPC.getMainClassification());
    }


    /**
     * 获取专利主分类{@link ClassificationIPCR}对象
     *
     * @param biblioData
     * @return
     */
    public static Optional<ClassificationIPCR> getPrimaryClassificationIPCR(BiblioData biblioData) {
        List<ClassificationIPCR> classificationIPCRs = biblioData.getClassificationIPCRs();

        return null == classificationIPCRs ? Optional.empty() : classificationIPCRs.stream()
                                                                                   .filter(ipcr -> ipcr.getSequence().equals("1"))
                                                                                   .findFirst();
    }

    //生成标题
    public static String generateTitle(Title title) {
        String titleStr = title.getTitle();
        return XmlUtils.removeTags(titleStr);
    }

    //生成摘要
    public static String generateAbs(Abs abs) {
        Node paragraph = abs.getParagraph();
        return XmlUtils.parseNode(paragraph).toString();
    }

    //生成Claim
    public static String generateClaim(Claims claims) {
        if (null == claims) {
            return "";
        }

        StringBuilder claimBuilder = claims.getClaimTexts()
                                           .stream()
                                           .collect(StringBuilder::new,
                                                    (builder, claimText) -> {
                                                        StringBuilder text = XmlUtils.parseNode(claimText);
                                                        builder.append(text);
                                                    }, StringBuilder::append);
        return claimBuilder.toString();
    }

    /**
     * 生成优先权日
     *
     * @param priorityClaimDetails
     */
    public static Date generatePriorityDate(List<DocumentIdInfo> priorityClaimDetails) {
        final Date dateTemp = new Date(0L);
        if (null != priorityClaimDetails && priorityClaimDetails.size() > 0) {
            priorityClaimDetails.stream()
                                .filter(docEntity -> {
                                    boolean isOriginal = Constants.XML_DATA_FORMAT_ORIGINAL.equals(docEntity.getDataFormat());
                                    if (isOriginal) {
                                        boolean isFirst = docEntity.isFirstPriority();
                                        return isFirst;
                                    }
                                    return false;
                                })
                                .findFirst()
                                .ifPresent(docEntity -> {
                                    Date date = docEntity.getDocumentID()
                                                         .getDate();
                                    dateTemp.setTime(date.getTime());
                                });
        }
        return dateTemp;
    }


    public static Map<String, String> generateDocIdInfo(List<DocumentIdInfo> list) {
        HashMap<String, String> map = new HashMap<>();
        if (null != list && list.size() > 0) {
            list.stream()
                .filter(appInfo -> "original".equals(appInfo.getDataFormat()))
                .findFirst()
                .ifPresent(idEntity -> {
                    DocumentID docId = idEntity.getDocumentID();
                    String country = docId.getCountry();
                    String number = docId.getNumber();
                    String kind = docId.getKind();
                    Date dateInfo = docId.getDate();
                    //logger.info("country is: " + country + ", number is: " + number + ", kind is: " + kind);
                    //logger.info("generate date is: " + dateInfo);
                    if (StringUtils.isEmpty(country) || StringUtils.isEmpty(number)) {
                        return;
                    }
                    if (null != dateInfo) {
                        StringBuilder builder = new StringBuilder(country);
                        builder.append(number);
                        if (!StringUtils.isEmpty(kind)) {
                            builder.append(kind);
                        }
                        map.put("id", builder.toString());
                        //logger.info("generate appId is: " + builder.toString());
                        map.put("date", dateInfo.getTime() + "");
                    }

                });
        }
        return map;
    }
}
