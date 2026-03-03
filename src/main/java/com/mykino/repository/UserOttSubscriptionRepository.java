package com.mykino.repository;

import com.mykino.entity.UserOttSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOttSubscriptionRepository extends JpaRepository<UserOttSubscription, Long> {

    List<UserOttSubscription> findByUserId(Long userId);

    boolean existsByUserIdAndOttPlatformId(Long userId, Long ottPlatformId);
}
