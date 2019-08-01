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
public abstract class UpdateRawDocSubscriber implements FlowableSubscriber<RawDoc>,
                                                        Consumer<RawDoc> {

    Logger logger = LoggerFactory.getLogger(UpdateRawDocSubscriber.class);

    private Subscription mSubscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(1);
        mSubscription = subscription;
    }

    @Override
    public void onNext(RawDoc rawDoc) {
        logger.info("on next get raw doc {}", rawDoc.getPubId());
        accept(rawDoc);
        mSubscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        logger.error("update raw doc subscription get error", t);
    }

    @Override
    public void onComplete() {
        logger.info("update raw docs on complete...");
    }
}
