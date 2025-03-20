package com.example.snsbackend.auth;

import com.example.snsbackend.model.Profile;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.contains("@")) {
            return new CustomUserDetails(profileRepository.findByEmail(username).orElseThrow());
        } else {
            return new CustomUserDetails(profileRepository.findByUsername(username).orElseThrow());
        }
    }

    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        return new CustomUserDetails(profileRepository.findById(userId).orElseThrow());
    }
}
