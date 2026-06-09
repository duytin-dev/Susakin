package com.susakin.app.dto.res.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VocabularyRes {

    private Long id;
    private Long topicId;
    private String topicName;
    private String word;
    private String meaningVi;
    private String imageUrl;
    private String audioUrl;
    private Integer orderIndex;
}
