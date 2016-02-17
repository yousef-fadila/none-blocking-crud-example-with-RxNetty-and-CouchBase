package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.dal;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.error.CASMismatchException;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Elyran Kogan
 * @since 3/24/2015
 */
@Component
public class CouchbaseWrapper {

    private static final Logger log = Logger.getLogger(CouchbaseWrapper.class.getName());

    @Value("${couchBase.persistTo}")
    private String persistToProperty;

    @Value("${couchBase.replicateTo}")
    private String replicateToProperty;

    @Value("${couchBase.timeout.milli:2000}")
    private int timeout;

    @Value("${entityMetadata.couchbase.maxRetries:3}")
    private int maxRetries;

    @Value("${entityMetadata.couchbase.ttl:0}")
    private int ttl;

    @Value("${couchBase.servers}")
    private String nodeList;

    @Value("${couchBase.bucketName}")
    private String bucketName;

    @Value("${couchBase.pass}")
    private String bucketPassword;

    private Bucket bucket;

    private ReplicateTo replicateTo;
    private PersistTo persistTo;

    private Gson gson = new Gson();

    @PostConstruct
    public void init() {
        CouchbaseCluster couchbaseCluster = CouchbaseCluster.create(Arrays.asList(nodeList.split(",")));
        bucket = couchbaseCluster.openBucket(bucketName, bucketPassword);

        persistTo = PersistTo.valueOf(persistToProperty);
        replicateTo = ReplicateTo.valueOf(replicateToProperty);
    }

    public <E> Observable<E> get(String id, Class<E> className) {
        return bucket.async()
                .get(id, RawJsonDocument.class)
                .retry(retryPredicate())
                .onErrorResumeNext(bucket.async().getFromReplica(id, ReplicaMode.ALL, RawJsonDocument.class))
                .timeout(timeout, TimeUnit.MILLISECONDS)
                .doOnError(e -> log.info("failed to get document: " + e.getMessage()))
                .map(toEntity(className));
    }

    public <E> Observable<Void> upsert(String id, E entity) {
        return bucket.async()
                .upsert(toRawJsonDocument(id, entity), persistTo, replicateTo)
                .timeout(timeout, TimeUnit.MILLISECONDS)
                .retry(retryPredicate())
                .doOnError(e -> log.info("failed to get document: " + e.getMessage()))
                .map(d -> null);
    }

    public <E> Observable<Void> remove(String id) {
        return bucket.async()
                .remove(id, persistTo, replicateTo, RawJsonDocument.class)
                .timeout(timeout, TimeUnit.MILLISECONDS)
                .retry(retryPredicate())
                .doOnError(e -> log.info("failed to get document: " + e.getMessage()))
                .map(d -> null);

    }

    private <E> RawJsonDocument toRawJsonDocument(String id, E entity) {
        String json = gson.toJson(entity);
        return RawJsonDocument.create(id, json);
    }


    private <E> Func1<RawJsonDocument, E> toEntity(Class<E> className) {
        return doc -> {
            String content = doc.content();
            E baseEntity = gson.fromJson(content, className);
            return baseEntity;
        };
    }

    private int getExpiry() {
        if (ttl == 0) return 0;
        return ((int)(System.currentTimeMillis() /1000)) + ttl;
    }

    /**
     * this predicate checks if there is a need to retry
     * the exceptions in here are not recoverable and there is no need to retry when getting them
     * @return predicate
     */
    private Func2<Integer, Throwable, Boolean> retryPredicate() {
        return (count, throwable) -> !(throwable instanceof CASMismatchException) &&
                !(throwable instanceof NoSuchElementException) &&
                !(throwable instanceof DocumentAlreadyExistsException) &&
                !(throwable instanceof DocumentDoesNotExistException) &&
                count < maxRetries;
    }


}

