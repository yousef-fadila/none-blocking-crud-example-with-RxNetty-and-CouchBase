package com.liveperson.interaction.infra.entitymetadata;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import rx.plugins.DebugHook;
import rx.plugins.DebugNotification;
import rx.plugins.DebugNotificationListener;
import rx.plugins.RxJavaPlugins;

import java.net.MalformedURLException;

@Configuration
@ComponentScan({"com.liveperson.interaction.infra.entitymetadata"})
public class TestConfig {

    private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

    @Bean
    static PropertyPlaceholderConfigurer propConfig() throws MalformedURLException {

//        RxJavaPlugins.getInstance().registerObservableExecutionHook(new DebugHook(new DebugNotificationListener() {
//            public Object onNext(DebugNotification n) {
//                log.info("onNext on "+n);
//                return super.onNext(n);
//            }
//
//
//            public Object start(DebugNotification n) {
//                log.info("start on "+n);
//                return super.start(n);
//            }
//
//
//            public void complete(Object context) {
//                super.complete(context);
//                log.info("onNext on "+context);
//            }
//
//            public void error(Object context, Throwable e) {
//                super.error(context, e);
//                log.info("error on "+context);
//            }
//        }));


        PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
        ppc.setLocation(new ClassPathResource("application.properties"));

        return ppc;
    }


}
