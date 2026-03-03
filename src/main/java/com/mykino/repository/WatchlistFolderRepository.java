package com.mykino.repository;

import com.mykino.entity.WatchlistFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistFolderRepository extends JpaRepository<WatchlistFolder, Long> {

    List<WatchlistFolder> findByUserIdOrderByCreatedAtDesc(Long userId);
}
