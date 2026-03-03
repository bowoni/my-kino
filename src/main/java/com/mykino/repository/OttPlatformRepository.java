package com.mykino.repository;

import com.mykino.entity.OttPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OttPlatformRepository extends JpaRepository<OttPlatform, Long> {

    Optional<OttPlatform> findByName(String name);

    Optional<OttPlatform> findByTmdbProviderId(Integer tmdbProviderId);

    List<OttPlatform> findByTmdbProviderIdIsNotNullOrderById();
}
