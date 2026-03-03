package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "watchlist_folders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WatchlistFolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    public WatchlistFolder(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
