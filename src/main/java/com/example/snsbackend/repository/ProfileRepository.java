package com.example.snsbackend.repository;

import com.example.snsbackend.model.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}
