package com.susakin.app.mapper;

import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.dto.res.topic.TopicRes;
import com.susakin.app.dto.res.vocabulary.UserVocabularyRes;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;
import com.susakin.app.dto.res.user.UserRes;
import com.susakin.app.entity.Lesson;
import com.susakin.app.entity.LessonProgress;
import com.susakin.app.entity.Topic;
import com.susakin.app.entity.User;
import com.susakin.app.entity.UserVocabulary;
import com.susakin.app.entity.Vocabulary;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static TopicRes toTopicRes(Topic topic) {
        return TopicRes.builder()
                .id(topic.getId())
                .name(topic.getName())
                .thumbnailUrl(topic.getThumbnailUrl())
                .orderIndex(topic.getOrderIndex())
                .build();
    }

    public static VocabularyRes toVocabularyRes(Vocabulary vocabulary) {
        Topic topic = vocabulary.getTopic();
        return VocabularyRes.builder()
                .id(vocabulary.getId())
                .topicId(topic.getId())
                .topicName(topic.getName())
                .word(vocabulary.getWord())
                .meaningVi(vocabulary.getMeaningVi())
                .imageUrl(vocabulary.getImageUrl())
                .audioUrl(vocabulary.getAudioUrl())
                .orderIndex(vocabulary.getOrderIndex())
                .build();
    }

    public static LessonRes toLessonRes(Lesson lesson) {
        Topic topic = lesson.getTopic();
        return LessonRes.builder()
                .id(lesson.getId())
                .topicId(topic.getId())
                .topicName(topic.getName())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .orderIndex(lesson.getOrderIndex())
                .build();
    }

    public static LessonProgressRes toLessonProgressRes(LessonProgress progress) {
        Lesson lesson = progress.getLesson();
        Topic topic = lesson.getTopic();
        return LessonProgressRes.builder()
                .id(progress.getId())
                .lessonId(lesson.getId())
                .lessonTitle(lesson.getTitle())
                .lessonType(lesson.getType())
                .topicId(topic.getId())
                .topicName(topic.getName())
                .status(progress.getStatus())
                .score(progress.getScore())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    public static UserVocabularyRes toUserVocabularyRes(UserVocabulary userVocabulary) {
        Vocabulary vocabulary = userVocabulary.getVocabulary();
        Topic topic = vocabulary.getTopic();
        return UserVocabularyRes.builder()
                .id(userVocabulary.getId())
                .vocabularyId(vocabulary.getId())
                .word(vocabulary.getWord())
                .meaningVi(vocabulary.getMeaningVi())
                .imageUrl(vocabulary.getImageUrl())
                .audioUrl(vocabulary.getAudioUrl())
                .topicId(topic.getId())
                .topicName(topic.getName())
                .status(userVocabulary.getStatus())
                .learnedAt(userVocabulary.getLearnedAt())
                .build();
    }

    public static UserRes toUserRes(User user) {
        return UserRes.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .grade(user.getGrade())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
