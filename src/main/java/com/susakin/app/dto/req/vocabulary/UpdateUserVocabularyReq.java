package com.susakin.app.dto.req.vocabulary;

import com.susakin.app.utils.enums.VocabStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserVocabularyReq {

    @NotNull(message = "Status is required")
    private VocabStatus status;
}
