package com.mykino.repository;

import com.mykino.entity.CastMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CastMemberRepository extends JpaRepository<CastMember, Long> {

    List<CastMember> findByNameContaining(String name);

    java.util.Optional<CastMember> findByTmdbId(Long tmdbId);
}
