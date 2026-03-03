package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ott_platforms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OttPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    private String logoUrl;

    private String baseUrl;

    private Integer tmdbProviderId;

    public OttPlatform(String name, String logoUrl, String baseUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.baseUrl = baseUrl;
    }
}
