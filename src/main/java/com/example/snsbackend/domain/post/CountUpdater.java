package com.example.snsbackend.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountUpdater {
    private final MongoTemplate mongoTemplate;

    public void incrementCount(String id, String fieldName, Class<?> clazz) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),
                new Update().inc(fieldName, 1), clazz);
    }

    public void decrementCount(String id, String fieldName, Class<?> clazz) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),
                new Update().inc(fieldName, -1), clazz);
    }
}
