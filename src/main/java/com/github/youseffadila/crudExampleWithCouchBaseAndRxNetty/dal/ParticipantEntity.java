package com.github.youseffadila.crudExampleWithCouchBaseAndRxNetty.dal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by youseff on 6/29/2015.
 */
public class ParticipantEntity {

    public final static String prefix ="ParticipantEntity-";
    private final static Gson gson = new Gson();

    public static class Entry {
        private String key;
        private String value;
        private long expiry;

        public Entry(String key, String value, long expiry) {
            this.key = key;
            this.value = value;
            this.expiry = expiry;
        }


        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }


        public long getExpiry() {
            return expiry;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry entry = (Entry) o;

            if (!key.equals(entry.key)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    public void setEntriesSet(Set<Entry> entriesSet) {
        this.entriesSet = entriesSet;
    }

    public Set<Entry> getEntriesSet() {
        return entriesSet;
    }

    private Set<Entry> entriesSet;

    public static ParticipantEntity fromJson(String jsonString){
        Set<ParticipantEntity.Entry> set = gson.fromJson(jsonString, new TypeToken<Set<Entry>>(){}.getType());
        ParticipantEntity participantEntity = new ParticipantEntity();
        participantEntity.setEntriesSet(set);
        return participantEntity;
    }

    //Override current entries with the new one in the set.
    public ParticipantEntity putEntries(Set<Entry> set){
        this.entriesSet.removeAll(set);
        set.addAll(this.entriesSet);
        this.entriesSet = set;
        return this;
    }
}
