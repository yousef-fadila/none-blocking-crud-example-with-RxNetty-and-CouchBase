package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.controller;



import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;


import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.logging.Logger;

/**
 * Created by youseff on 6/29/2015.
 */
@Component
public class HealthCheckController implements RequestHandler<ByteBuf, ByteBuf> {
    private static final Logger log = Logger.getLogger(HealthCheckController.class.getName());

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        log.info("health check is called");
        //TODO check couchbase availability
        response.setStatus(HttpResponseStatus.OK);
        return response.close(true);
    }
}