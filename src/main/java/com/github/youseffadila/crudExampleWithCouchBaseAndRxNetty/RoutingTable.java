package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty;

import com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.controller.HealthCheckController;
import com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.controller.Notfound404Controller;
import com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.controller.ParticipantController;
import io.netty.buffer.ByteBuf;
import netflix.karyon.transport.http.SimpleUriRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by youseff on 6/29/2015.
 */
@Component
public class RoutingTable {

    SimpleUriRouter<ByteBuf, ByteBuf> router = new SimpleUriRouter<>();;

    @Autowired
    ParticipantController participantController;

    @Autowired
    HealthCheckController healthCheckController;

    @PostConstruct
    private void buildRoute(){
        // first match policy!
        router.addUri("/health", healthCheckController);
        router.addUri("/participant/*", participantController);
        router.addUri("/*", new Notfound404Controller());
    }

    public SimpleUriRouter<ByteBuf, ByteBuf> getRouter() {
        return router;
    }
}
