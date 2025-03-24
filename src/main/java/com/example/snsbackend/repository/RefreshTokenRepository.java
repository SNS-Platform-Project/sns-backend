package com.example.snsbackend.repository;

import com.example.snsbackend.model.RefreshToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    void deleteByUserId(@NotBlank String userId);

    Optional<RefreshToken> findByUserId(String userId);
}
