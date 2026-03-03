package com.mykino.repository;

import com.mykino.entity.ContentOtt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentOttRepository extends JpaRepository<ContentOtt, Long> {

    List<ContentOtt> findByContentId(Long contentId);

    // 특정 OTT에서 스트리밍 가능한 콘텐츠
    @Query("SELECT co FROM ContentOtt co WHERE co.ottPlatform.id = :ottId AND co.isStreaming = true")
    List<ContentOtt> findStreamingByOttPlatformId(@Param("ottId") Long ottId);
}
