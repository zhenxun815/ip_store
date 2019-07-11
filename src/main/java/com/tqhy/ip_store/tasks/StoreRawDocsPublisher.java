package com.tqhy.ip_store.tasks;

import com.tqhy.ip_store.models.mongo.RawDoc;
import com.tqhy.ip_store.utils.XmlUtils;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @author Yiheng
 * @create 4/2/2019
 * @since 1.0.0
 */
@Getter
@Setter
@RequiredArgsConstructor(staticName = "with")
public class StoreRawDocsPublisher implements FlowableOnSubscribe<RawDoc> {
    Logger logger = LoggerFactory.getLogger(StoreRawDocsPublisher.class);

    @NonNull
    private String baseDirPath;

    @NonNull
    private String[] biblioBasePathArr;

    @Override
    public void subscribe(FlowableEmitter<RawDoc> emitter) throws Exception {
        Iterator<File> xmlDirIterator = XmlUtils.getAllXmlDirUnderBaseDir(baseDirPath, biblioBasePathArr);

        while (xmlDirIterator.hasNext()) {
            if (emitter.requested() == 0) {
                logger.info("emitter requested is 0...");
                continue;
            }
            File xmlDir = xmlDirIterator.next();
            logger.info("start parse xml {}", xmlDir.getAbsolutePath());
            XmlUtils.getRawDocFromXml(xmlDir)
                    .ifPresent(rawDoc -> emitter.onNext(rawDoc));
        }
        emitter.onComplete();
    }
}
