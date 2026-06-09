package com.susakin.app.service.impl;

import com.susakin.app.dto.req.topic.TopicReq;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.dto.res.topic.TopicRes;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;
import com.susakin.app.entity.Lesson;
import com.susakin.app.entity.Topic;
import com.susakin.app.entity.Vocabulary;
import com.susakin.app.exception.BadRequestException;
import com.susakin.app.exception.ResourceNotFoundException;
import com.susakin.app.mapper.EntityMapper;
import com.susakin.app.repository.LessonProgressRepository;
import com.susakin.app.repository.LessonRepository;
import com.susakin.app.repository.TopicRepository;
import com.susakin.app.repository.UserVocabularyRepository;
import com.susakin.app.repository.VocabularyRepository;
import com.susakin.app.service.TopicService;
import com.susakin.app.utils.enums.LessonType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final VocabularyRepository vocabularyRepository;
    private final LessonRepository lessonRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TopicRes> getAll(String name, Integer orderIndex) {
        return topicRepository.findAll(buildTopicSpec(name, orderIndex), Sort.by("orderIndex")).stream()
                .map(EntityMapper::toTopicRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TopicRes getById(Long id) {
        return getTopicEntity(id).map(EntityMapper::toTopicRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));
    }

    @Override
    @Transactional
    public TopicRes create(TopicReq request) {
        Topic topic = new Topic();
        applyTopicFields(topic, request);
        return EntityMapper.toTopicRes(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public TopicRes update(Long id, TopicReq request) {
        Topic topic = getTopicEntity(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));
        applyTopicFields(topic, request);
        return EntityMapper.toTopicRes(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Topic topic = getTopicEntity(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));

        List<Vocabulary> vocabularies = vocabularyRepository.findByTopicIdOrderByOrderIndexAsc(id);
        vocabularies.forEach(v -> userVocabularyRepository.deleteByVocabularyId(v.getId()));
        vocabularyRepository.deleteAll(vocabularies);

        List<Lesson> lessons = lessonRepository.findByTopicIdOrderByOrderIndexAsc(id);
        lessons.forEach(l -> lessonProgressRepository.deleteByLessonId(l.getId()));
        lessonRepository.deleteAll(lessons);

        topicRepository.delete(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VocabularyRes> getVocabulariesByTopic(Long topicId, String keyword) {
        ensureTopicExists(topicId);
        return vocabularyRepository.findAll(buildVocabularySpec(topicId, keyword), Sort.by("orderIndex")).stream()
                .map(EntityMapper::toVocabularyRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonRes> getLessonsByTopic(Long topicId, String type) {
        ensureTopicExists(topicId);
        LessonType lessonType = null;
        if (StringUtils.hasText(type)) {
            try {
                lessonType = LessonType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Loại bài học không hợp lệ");
            }
        }
        return lessonRepository.findAll(buildLessonSpec(topicId, lessonType, null), Sort.by("orderIndex")).stream()
                .map(EntityMapper::toLessonRes)
                .toList();
    }

    private java.util.Optional<Topic> getTopicEntity(Long id) {
        return topicRepository.findById(id);
    }

    private void applyTopicFields(Topic topic, TopicReq request) {
        topic.setName(request.getName());
        topic.setThumbnailUrl(request.getThumbnailUrl());
        topic.setOrderIndex(request.getOrderIndex());
    }

    private void ensureTopicExists(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Không tìm thấy topic");
        }
    }

    private Specification<Topic> buildTopicSpec(String name, Integer orderIndex) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (orderIndex != null) {
                predicates.add(cb.equal(root.get("orderIndex"), orderIndex));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Vocabulary> buildVocabularySpec(Long topicId, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("word")), pattern),
                        cb.like(cb.lower(root.get("meaningVi")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Lesson> buildLessonSpec(Long topicId, LessonType type, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
