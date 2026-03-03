package com.mykino.entity;

import com.mykino.enums.CastRoleType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "content_cast")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentCast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cast_member_id", nullable = false)
    private CastMember castMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CastRoleType roleType;

    @Column(length = 100)
    private String characterName;

    private Integer castOrder;

    public ContentCast(Content content, CastMember castMember,
                       CastRoleType roleType, String characterName, Integer castOrder) {
        this.content = content;
        this.castMember = castMember;
        this.roleType = roleType;
        this.characterName = characterName;
        this.castOrder = castOrder;
    }
}
