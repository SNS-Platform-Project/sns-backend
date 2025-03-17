package com.example.snsbackend.repository;

import com.example.snsbackend.model.AuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuthCodeRepository extends MongoRepository<AuthCode, String> {
    Optional<AuthCode> findByEmailAndAuthCode(String email, String authCode);
    Optional<AuthCode> findByEmail(String email);

    List<AuthCode> findByExpiredAtBefore(LocalDateTime expiredAtBefore);

    void deleteByEmail(String email);
}
