package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cast_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CastMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String nameEng;

    private String profileImage;

    @Column(unique = true)
    private Long tmdbId;

    @Builder
    public CastMember(String name, String nameEng, String profileImage, Long tmdbId) {
        this.name = name;
        this.nameEng = nameEng;
        this.profileImage = profileImage;
        this.tmdbId = tmdbId;
    }
}
