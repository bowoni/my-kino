package com.mykino.entity;

import com.mykino.enums.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 300)
    private String message;

    private Long referenceId;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Builder
    public Notification(User user, NotificationType type, String message, Long referenceId) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
