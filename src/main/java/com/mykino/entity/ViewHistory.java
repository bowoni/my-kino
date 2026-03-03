package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "view_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    private LocalDate watchDate;

    @Column(length = 500)
    private String memo;

    @Builder
    public ViewHistory(User user, Content content, LocalDate watchDate, String memo) {
        this.user = user;
        this.content = content;
        this.watchDate = watchDate != null ? watchDate : LocalDate.now();
        this.memo = memo;
    }

    public void update(LocalDate watchDate, String memo) {
        this.watchDate = watchDate;
        this.memo = memo;
    }
}
