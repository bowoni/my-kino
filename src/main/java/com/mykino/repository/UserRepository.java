package com.mykino.repository;

import com.mykino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mykino.enums.AuthProvider;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    Optional<User> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
