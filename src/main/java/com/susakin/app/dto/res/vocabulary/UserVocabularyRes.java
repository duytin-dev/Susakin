package com.susakin.app.dto.res.vocabulary;

import com.susakin.app.utils.enums.VocabStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserVocabularyRes {

    private Long id;
    private Long vocabularyId;
    private String word;
    private String meaningVi;
    private String imageUrl;
    private String audioUrl;
    private Long topicId;
    private String topicName;
    private VocabStatus status;
    private LocalDateTime learnedAt;
}
