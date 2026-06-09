package com.susakin.app.dto.req.vocabulary;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveUserVocabularyReq {

    @NotNull(message = "Vocabulary ID is required")
    private Long vocabularyId;
}
