package com.susakin.app.repository;

import com.susakin.app.entity.UserVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long>, JpaSpecificationExecutor<UserVocabulary> {

    List<UserVocabulary> findByUserIdOrderByLearnedAtDesc(Long userId);

    Optional<UserVocabulary> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    void deleteByVocabularyId(Long vocabularyId);
}
