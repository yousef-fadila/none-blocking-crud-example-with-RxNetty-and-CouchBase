package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.controller;


import com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.model.ParticipantModel;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by youseff on 6/29/2015.
 */

@Component
public class ParticipantController implements RequestHandler<ByteBuf, ByteBuf> {
    private static final Logger log = Logger.getLogger(HealthCheckController.class.getName());
    private static final Gson gson = new Gson();
    private static final String FIELDS_QUERY_PARAM = "fields";
    private static final String MODE_QUERY_PARAM = "mode";
    private static final String MERGE_FIELD_MODE = "merge";

    @Autowired
    ParticipantModel participantModel;

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        final String id = extractId(request.getPath());

        if (request.getHttpMethod() == HttpMethod.GET) {
            List<String> fields = getFieldsParam(request);
            return participantModel.getParticipantDataById(id, fields).flatMap(content -> {
                response.writeString(content);
                return response.close(true);
            });

        } else if (request.getHttpMethod() == HttpMethod.PUT && isMergeFieldParam(request)) {
            log.info("PUT is invoked with isMergeFieldParam ");
            return  request.getContent().map(byteBuf -> byteBuf.toString(Charset.forName("UTF-8")))
                   .flatMap(requestBody -> participantModel.updateParticipantFields(id, requestBody))
                   .doOnCompleted(()->response.close());


        } else if (request.getHttpMethod() == HttpMethod.PUT && !isMergeFieldParam(request)) {
            log.info("PUT is invoked with !isMergeFieldParam ");
            return  request.getContent().map(byteBuf -> byteBuf.toString(Charset.forName("UTF-8")))
                    .map(requestBody -> participantModel.putParticipantData(id, requestBody))
                    .flatMap(m -> response.close());

        } else if (request.getHttpMethod() == HttpMethod.DELETE) {
            return participantModel.deleteParticipantData(id).flatMap(e->response.close());

        } else if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            response.setStatus(HttpResponseStatus.OK);
            return response.close();

        } else {
            response.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
            return response.close();
        }
    }

    private boolean isMergeFieldParam(HttpServerRequest<ByteBuf> request) {
        List<String> modeValues = request.getQueryParameters().get(MODE_QUERY_PARAM);
        if (modeValues != null && !modeValues.isEmpty() && modeValues.get(0).equals(MERGE_FIELD_MODE)){
            return true;
        } else {
            return false;
        }
    }

    private List<String> getFieldsParam(HttpServerRequest<ByteBuf> request) {
        List<String> fields = request.getQueryParameters().get(FIELDS_QUERY_PARAM);
        if (fields != null && !fields.isEmpty()) {
            return Arrays.asList(fields.get(0).split(","));
        } else {
            return null;
        }
    }

    private String extractId(String path) {
        String[] split = path.split("/|\\?");
        // the controller deal with /participant/ path, so path split must return >=3
        assert(split.length >=3);
        return split[2];
    }
}