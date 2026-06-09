package com.susakin.app.repository;

import com.susakin.app.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long>, JpaSpecificationExecutor<LessonProgress> {

    List<LessonProgress> findByUserIdOrderByCompletedAtDesc(Long userId);

    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    Optional<LessonProgress> findByIdAndUserId(Long id, Long userId);

    void deleteByLessonId(Long lessonId);
}
