package com.cdl.epms.repository;

import com.cdl.epms.model.GoalMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalMasterRepository extends JpaRepository<GoalMaster, Long> {

    Optional<GoalMaster> findById(Long id);

    // Get all active goals ordered by category and display order
    List<GoalMaster> findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc();

    // Get goals by specific category
    List<GoalMaster> findByCategoryOrderByDisplayOrderAsc(String category);

    // Get active goals by specific category
    List<GoalMaster> findByIsActiveTrueAndCategoryOrderByDisplayOrderAsc(String category);

    // Get all distinct categories
    @Query("SELECT DISTINCT gm.category FROM GoalMaster gm WHERE gm.isActive = true ORDER BY gm.category")
    List<String> findDistinctActiveCategories();

    // Check if differentiator name exists (case insensitive)
    boolean existsByDifferentiatorNameIgnoreCase(String differentiatorName);

    // Get all goals grouped by category (ordered)
    @Query("SELECT gm FROM GoalMaster gm ORDER BY gm.category, gm.displayOrder")
    List<GoalMaster> findAllGroupedByCategory();

    // Get active goals grouped by category
    @Query("SELECT gm FROM GoalMaster gm WHERE gm.isActive = true ORDER BY gm.category, gm.displayOrder")
    List<GoalMaster> findActiveGroupedByCategory();

    // Get categories with counts
    @Query("SELECT gm.category, COUNT(gm) FROM GoalMaster gm WHERE gm.isActive = true GROUP BY gm.category ORDER BY gm.category")
    List<Object[]> getCategoryCounts();
}