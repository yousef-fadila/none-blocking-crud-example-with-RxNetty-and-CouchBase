package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.controller;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

import java.util.logging.Logger;

/**
 * Created by youseff on 6/29/2015.
 */
public class Notfound404Controller implements RequestHandler<ByteBuf, ByteBuf> {
    private static final Logger log = Logger.getLogger(HealthCheckController.class.getName());

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        log.info("Notfound404Handler is called ");
        response.setStatus(HttpResponseStatus.NOT_FOUND);
        return response.close(true);
    }
}