package com.susakin.app.dto.res.lesson;

import com.susakin.app.utils.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LessonRes {

    private Long id;
    private Long topicId;
    private String topicName;
    private String title;
    private LessonType type;
    private Integer orderIndex;
}
