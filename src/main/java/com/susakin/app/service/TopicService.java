package com.susakin.app.service;

import com.susakin.app.dto.req.topic.TopicReq;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.dto.res.topic.TopicRes;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;

import java.util.List;

public interface TopicService {

    List<TopicRes> getAll(String name, Integer orderIndex);

    TopicRes getById(Long id);

    TopicRes create(TopicReq request);

    TopicRes update(Long id, TopicReq request);

    void delete(Long id);

    List<VocabularyRes> getVocabulariesByTopic(Long topicId, String keyword);

    List<LessonRes> getLessonsByTopic(Long topicId, String type);
}
