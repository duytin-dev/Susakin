package com.susakin.app.dto.res.lesson;

import com.susakin.app.utils.enums.LessonType;
import com.susakin.app.utils.enums.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class LessonProgressRes {

    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private LessonType lessonType;
    private Long topicId;
    private String topicName;
    private ProgressStatus status;
    private Integer score;
    private LocalDateTime completedAt;
}
