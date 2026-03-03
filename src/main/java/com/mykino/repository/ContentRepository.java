package com.mykino.repository;

import com.mykino.entity.Content;
import com.mykino.enums.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    // 제목 검색 (한글 + 영문)
    @Query("SELECT c FROM Content c WHERE c.title LIKE %:keyword% OR c.titleEng LIKE %:keyword%")
    Page<Content> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 자동완성용 (상위 10개)
    @Query("SELECT c FROM Content c WHERE c.title LIKE :keyword% OR c.titleEng LIKE :keyword% ORDER BY c.totalRatings DESC")
    List<Content> findTop10ByKeyword(@Param("keyword") String keyword);

    // 콘텐츠 타입별 조회
    Page<Content> findByContentType(ContentType contentType, Pageable pageable);

    // 랭킹 (평가 수 기준 상위)
    List<Content> findTop10ByOrderByTotalRatingsDesc();

    // 키노 점수 높은 순
    List<Content> findTop10ByKinoColorNotNullOrderByKinoScoreDesc();

    // 장르별 조회
    @Query("SELECT c FROM Content c JOIN c.contentGenres cg WHERE cg.genre.id = :genreId")
    Page<Content> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    // 최신순
    Page<Content> findAllByOrderByReleaseYearDesc(Pageable pageable);

    // TMDB ID로 조회
    boolean existsByTmdbId(Long tmdbId);

    java.util.Optional<Content> findByTmdbId(Long tmdbId);

    boolean existsByTmdbIdAndContentType(Long tmdbId, ContentType contentType);

    java.util.Optional<Content> findByTmdbIdAndContentType(Long tmdbId, ContentType contentType);
}
