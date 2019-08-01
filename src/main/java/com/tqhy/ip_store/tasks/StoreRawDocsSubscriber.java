package com.tqhy.ip_store.tasks;


import com.tqhy.ip_store.models.mongo.RawDoc;
import io.reactivex.FlowableSubscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Yiheng
 * @create 7/11/2019
 * @since 1.0.0
 */
public abstract class StoreRawDocsSubscriber implements FlowableSubscriber<RawDoc>,
                                                        Function<List<RawDoc>, List<RawDoc>> {

    Logger logger = LoggerFactory.getLogger(StoreRawDocsSubscriber.class);

    private List<RawDoc> rawDocs = new ArrayList<>();
    private Subscription mSubscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(1);
        mSubscription = subscription;
    }

    @Override
    public void onNext(RawDoc rawDoc) {
        logger.info("on next get raw doc {}", rawDoc.getPubId());
        rawDocs.add(rawDoc);
        logger.info("on next size {}", rawDocs.size());
        if (rawDocs.size() == 5000) {
            List<RawDoc> saveDocs = apply(rawDocs);
            logger.info("on next save ... {}", saveDocs.size());
            rawDocs.clear();
        }
        mSubscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        logger.error("save raw doc subscription get error", t);
    }

    @Override
    public void onComplete() {
        logger.info("on complete...");
        if (rawDocs.size() > 0) {
            List<RawDoc> saveDocs = apply(rawDocs);
            logger.info("on complete save ... {}", saveDocs.size());
            rawDocs.clear();
        }
    }
}
