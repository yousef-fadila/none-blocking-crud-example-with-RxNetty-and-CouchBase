package com.liveperson.interaction.infra.entitymetadata;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * Created by youseff on 6/29/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DummyTests {
    private static final Logger log = LoggerFactory.getLogger(DummyTests.class);

    HttpClient<ByteBuf, ByteBuf> client = RxNetty.createHttpClient("localhost", 8889);

    @Test
    public void putReadUpdateTest() throws Exception {
        String content = "[{\"key\":\"name\",\"value\":\"test\",\"expiry\":0}]";

        client.submit(HttpClientRequest.createPut("/participant/test").withContent(content))
                .flatMap(response -> response.getContent().map(rcontent-> rcontent.toString(Charset.defaultCharset())))
                .toBlocking().singleOrDefault(null);

        String getResposnse = client.submit(HttpClientRequest.createGet("/participant/test"))
                .flatMap(response -> response.getContent().map(rcontent -> rcontent.toString(Charset.defaultCharset())))
                .toBlocking()
                .first();

        log.debug("getResponse: " + getResposnse);

        Assert.isTrue(content.equals(getResposnse));

        String updatedContent = "[{\"key\":\"name\",\"value\":\"updated\",\"expiry\":0}]";

        client.submit(HttpClientRequest.createPut("/participant/test?mode=merge").withContent(updatedContent))
                .flatMap(response -> response.getContent().map(rcontent -> rcontent.toString(Charset.defaultCharset())))
                .toBlocking()
                .singleOrDefault(null);

        String updatedResponse = client.submit(HttpClientRequest.createGet("/participant/test"))
                .flatMap(response -> response.getContent().map(rcontent -> rcontent.toString(Charset.defaultCharset())))
                .toBlocking()
                .first();

        log.debug("updatedResponse: " + updatedResponse);
        Assert.isTrue(updatedContent.equals(updatedResponse));
    }

    @Test
    public void runTheServerFor10Minutes() throws InterruptedException {
        Thread.sleep(10*60*1000);
        log.debug("server is down.");
    }
}
