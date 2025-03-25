package com.example.snsbackend.repository;

import com.example.snsbackend.model.AccessTokenBlackList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccessTokenBlackListRepository extends MongoRepository<AccessTokenBlackList, String> {
    Optional<AccessTokenBlackList> findByAccessToken(String accessToken);

    Iterable<? extends AccessTokenBlackList> findByBlackAtBefore(LocalDateTime blackAtBefore);
}
