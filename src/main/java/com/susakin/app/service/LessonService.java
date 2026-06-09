package com.susakin.app.service;

import com.susakin.app.dto.req.lesson.LessonProgressReq;
import com.susakin.app.dto.req.lesson.LessonReq;
import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.utils.enums.LessonType;

import java.util.List;

public interface LessonService {

    List<LessonRes> getAll(Long topicId, LessonType type, String keyword);

    LessonRes getById(Long id);

    LessonRes create(LessonReq request);

    LessonRes update(Long id, LessonReq request);

    void delete(Long id);

    LessonProgressRes saveProgress(Long lessonId, LessonProgressReq request);
}
