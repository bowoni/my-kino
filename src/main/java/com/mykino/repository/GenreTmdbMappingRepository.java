package com.mykino.repository;

import com.mykino.entity.GenreTmdbMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreTmdbMappingRepository extends JpaRepository<GenreTmdbMapping, Long> {

    Optional<GenreTmdbMapping> findByTmdbGenreId(Integer tmdbGenreId);
}
