package com.susakin.app.service.impl;

import com.susakin.app.dto.req.lesson.LessonProgressReq;
import com.susakin.app.dto.req.lesson.LessonReq;
import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.entity.Lesson;
import com.susakin.app.entity.LessonProgress;
import com.susakin.app.entity.Topic;
import com.susakin.app.entity.User;
import com.susakin.app.exception.ResourceNotFoundException;
import com.susakin.app.mapper.EntityMapper;
import com.susakin.app.repository.LessonProgressRepository;
import com.susakin.app.repository.LessonRepository;
import com.susakin.app.repository.TopicRepository;
import com.susakin.app.repository.UserRepository;
import com.susakin.app.security.UserPrincipal;
import com.susakin.app.service.LessonService;
import com.susakin.app.utils.SecurityUtils;
import com.susakin.app.utils.enums.LessonType;
import com.susakin.app.utils.enums.ProgressStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LessonRes> getAll(Long topicId, LessonType type, String keyword) {
        return lessonRepository.findAll(buildSpec(topicId, type, keyword), Sort.by("orderIndex")).stream()
                .map(EntityMapper::toLessonRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LessonRes getById(Long id) {
        return lessonRepository.findById(id)
                .map(EntityMapper::toLessonRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));
    }

    @Override
    @Transactional
    public LessonRes create(LessonReq request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));

        Lesson lesson = new Lesson();
        applyFields(lesson, request, topic);
        return EntityMapper.toLessonRes(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public LessonRes update(Long id, LessonReq request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));

        applyFields(lesson, request, topic);
        return EntityMapper.toLessonRes(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bài học");
        }
        lessonProgressRepository.deleteByLessonId(id);
        lessonRepository.deleteById(id);
    }

    @Override
    @Transactional
    public LessonProgressRes saveProgress(Long lessonId, LessonProgressReq request) {
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        LessonProgress progress = lessonProgressRepository
                .findByUserIdAndLessonId(currentUser.getId(), lessonId)
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    newProgress.setUser(user);
                    newProgress.setLesson(lesson);
                    return newProgress;
                });

        progress.setStatus(request.getStatus());
        progress.setScore(request.getScore());
        progress.setCompletedAt(request.getStatus() == ProgressStatus.COMPLETED ? LocalDateTime.now() : null);

        return EntityMapper.toLessonProgressRes(lessonProgressRepository.save(progress));
    }

    private void applyFields(Lesson lesson, LessonReq request, Topic topic) {
        lesson.setTopic(topic);
        lesson.setTitle(request.getTitle());
        lesson.setType(request.getType());
        lesson.setOrderIndex(request.getOrderIndex());
    }

    private Specification<Lesson> buildSpec(Long topicId, LessonType type, String keyword) {
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
