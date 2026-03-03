package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "explore_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExploreCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String categoryKey;

    @Column(nullable = false, length = 30)
    private String label;

    @Column(nullable = false, length = 10)
    private String mediaType;

    @Column(length = 50)
    private String baseGenre;

    @Column(nullable = false)
    private Integer sortOrder;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sortOrder")
    private List<ExploreCategoryGenre> subGenres = new ArrayList<>();
}
