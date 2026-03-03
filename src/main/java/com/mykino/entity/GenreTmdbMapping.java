package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "genre_tmdb_mappings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenreTmdbMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(nullable = false, unique = true)
    private Integer tmdbGenreId;
}
