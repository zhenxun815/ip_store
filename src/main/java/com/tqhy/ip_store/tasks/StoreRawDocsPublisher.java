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
    private File workBiblioDir;

    @Override
    public void subscribe(FlowableEmitter<RawDoc> emitter) throws Exception {
        File[] yearDirs = workBiblioDir.listFiles(File::isDirectory);
        logger.info("biblio dirs in {} collection start...", workBiblioDir.getAbsolutePath());
        List<File> xmlDirs = Arrays.stream(yearDirs)
                                   .collect(ArrayList::new,
                                            (list, yearDir) -> {
                                                File[] xmlDirArr = XmlUtils.getXmlDirs(yearDir);
                                                list.addAll(Arrays.asList(xmlDirArr));
                                            },
                                            ArrayList::addAll);
        Iterator<File> xmlDirIterator = xmlDirs.iterator();

        logger.info("biblio dirs in {} collection complete with count {}...",
                    workBiblioDir.getAbsolutePath(),
                    xmlDirs.size());

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
