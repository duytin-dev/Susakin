package com.susakin.app.dto.req.lesson;

import com.susakin.app.utils.enums.ProgressStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonProgressReq {

    @NotNull(message = "Status is required")
    private ProgressStatus status;

    private Integer score;
}
