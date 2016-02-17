package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.server.ErrorHandler;
import netflix.karyon.transport.KaryonTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.logging.Logger;

/**
 * Created by youseff on 6/29/2015.
 */
@Component
public class HttpServer {
    private static final Logger log = Logger.getLogger(HttpServer.class.getName());

    @Value("${entityMetadata.HttpServer.port}")
    int port;

    @Value("${entityMetadata.HttpServer.requestProcessingThreadsCount:32}")
    int RequestProcessingThreadsCount;

    @Autowired
    RoutingTable routingTable;
    private io.reactivex.netty.protocol.http.server.HttpServer<ByteBuf, ByteBuf> server;

    @PostConstruct
    public void start(){
        server = KaryonTransport.newHttpServerBuilder(port, routingTable.getRouter())
        .withRequestProcessingThreads(RequestProcessingThreadsCount)
        .build();

        server.withErrorHandler(new ErrorHandler() {
            @Override
            public Observable<Void> handleError(Throwable throwable) {
                log.info("catches throwable in  http server: " + throwable.getMessage());
                return Observable.empty();
            }
        })
        .start();
    }

    @PreDestroy
    public void shutdown(){
        try {
            server.shutdown();
        } catch (InterruptedException e) {
            log.info("exception in shutdown" + e.getMessage());
        }
    }
}
