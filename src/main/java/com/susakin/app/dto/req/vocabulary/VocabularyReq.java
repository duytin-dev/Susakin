package com.susakin.app.dto.req.vocabulary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VocabularyReq {

    @NotBlank(message = "Word is required")
    private String word;

    @NotBlank(message = "Meaning is required")
    private String meaningVi;

    private String imageUrl;

    private String audioUrl;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    @NotNull(message = "Topic ID is required")
    private Long topicId;
}
