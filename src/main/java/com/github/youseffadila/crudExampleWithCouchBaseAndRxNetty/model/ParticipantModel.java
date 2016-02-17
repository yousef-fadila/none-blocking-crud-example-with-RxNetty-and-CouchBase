package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.model;

import com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.dal.CouchbaseWrapper;
import com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.dal.ParticipantEntity;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by youseff on 6/29/2015.
 */

@Component
public class ParticipantModel {
    private final static Gson gson = new Gson();

    @Autowired
    CouchbaseWrapper couchbaseWrapper;

    public Observable<String> getParticipantDataById(String id, List<String> fields){
        return couchbaseWrapper.get(ParticipantEntity.prefix + id, ParticipantEntity.class)
                .map(e -> e.getEntriesSet())
                .map(lst -> fields == null? lst: lst.stream().filter(entry-> fields.contains(entry.getKey())).collect(Collectors.toList()))
                .map(lst-> gson.toJson(lst));
    }

    public Observable<Void>  putParticipantData(String id, String jsonData) {
        return couchbaseWrapper.upsert(ParticipantEntity.prefix + id, ParticipantEntity.fromJson(jsonData));
    }

    public Observable<Void>  updateParticipantFields(String id, String jsonData) {
        final ParticipantEntity participantEntity = ParticipantEntity.fromJson(jsonData);

        return couchbaseWrapper.get(ParticipantEntity.prefix + id, ParticipantEntity.class)
                .singleOrDefault(participantEntity)
                .map(participant -> participant.putEntries(participantEntity.getEntriesSet()))
                .flatMap(participant -> couchbaseWrapper.upsert(ParticipantEntity.prefix + id, participant));
    }

    public Observable<Void>  deleteParticipantData(String id) {
        return couchbaseWrapper.remove(ParticipantEntity.prefix + id);
    }
}