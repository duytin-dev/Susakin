package com.susakin.app.dto.req.lesson;

import com.susakin.app.utils.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonReq {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Type is required")
    private LessonType type;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    @NotNull(message = "Topic ID is required")
    private Long topicId;
}
