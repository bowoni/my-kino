package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "explore_category_genres")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExploreCategoryGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExploreCategory category;

    @Column(nullable = false)
    private Integer tmdbGenreId;

    @Column(nullable = false, length = 30)
    private String genreName;

    @Column(nullable = false)
    private Integer sortOrder;
}
