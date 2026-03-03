package com.mykino.repository;

import com.mykino.entity.ExploreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExploreCategoryRepository extends JpaRepository<ExploreCategory, Long> {

    List<ExploreCategory> findAllByOrderBySortOrder();
}
