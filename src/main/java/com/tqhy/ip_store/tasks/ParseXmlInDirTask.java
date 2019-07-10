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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Yiheng
 * @create 4/2/2019
 * @since 1.0.0
 */
@Getter
@Setter
@RequiredArgsConstructor(staticName = "with")
public class ParseXmlInDirTask implements FlowableOnSubscribe<RawDoc> {
    Logger logger = LoggerFactory.getLogger(ParseXmlInDirTask.class);

    @NonNull
    private File yearDir;

    @Override
    public void subscribe(FlowableEmitter<RawDoc> emitter) throws Exception {
        logger.info("working yearDir is: {}", yearDir.getAbsolutePath());
        File[] xmlDirs = XmlUtils.getXmlDirs(yearDir);
        for (File xmlDir : xmlDirs) {
            XmlUtils.getRawDocFromXml(xmlDir)
                    .ifPresent(rawDoc -> emitter.onNext(rawDoc));
        }
        emitter.onComplete();
    }
}
