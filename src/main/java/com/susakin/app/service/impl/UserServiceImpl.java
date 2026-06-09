package com.susakin.app.service.impl;

import com.susakin.app.dto.req.user.UserAdminReq;
import com.susakin.app.dto.req.user.UserUpdateReq;
import com.susakin.app.dto.req.vocabulary.SaveUserVocabularyReq;
import com.susakin.app.dto.req.vocabulary.UpdateUserVocabularyReq;
import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.user.UserRes;
import com.susakin.app.dto.res.vocabulary.UserVocabularyRes;
import com.susakin.app.entity.LessonProgress;
import com.susakin.app.entity.User;
import com.susakin.app.entity.UserVocabulary;
import com.susakin.app.entity.Vocabulary;
import com.susakin.app.exception.BadRequestException;
import com.susakin.app.exception.ResourceNotFoundException;
import com.susakin.app.mapper.EntityMapper;
import com.susakin.app.repository.LessonProgressRepository;
import com.susakin.app.repository.UserRepository;
import com.susakin.app.repository.UserVocabularyRepository;
import com.susakin.app.repository.VocabularyRepository;
import com.susakin.app.security.UserPrincipal;
import com.susakin.app.service.UserService;
import com.susakin.app.utils.SecurityUtils;
import com.susakin.app.utils.enums.ProgressStatus;
import com.susakin.app.utils.enums.VocabStatus;
import jakarta.persistence.criteria.Join;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;

    @Override
    @Transactional(readOnly = true)
    public UserRes getMe() {
        return getUserEntity(SecurityUtils.getCurrentUser().getId())
                .map(EntityMapper::toUserRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
    }

    @Override
    @Transactional
    public UserRes updateMe(UserUpdateReq request) {
        User user = getUserEntity(SecurityUtils.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        user.setName(request.getName());
        user.setGrade(request.getGrade());
        return EntityMapper.toUserRes(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRes> getAll(String email, String name, Integer grade) {
        return userRepository.findAll(buildUserSpec(email, name, grade), Sort.by("id")).stream()
                .map(EntityMapper::toUserRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserRes getById(Long id) {
        return getUserEntity(id)
                .map(EntityMapper::toUserRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
    }

    @Override
    @Transactional
    public UserRes update(Long id, UserAdminReq request) {
        User user = getUserEntity(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setGrade(request.getGrade());
        return EntityMapper.toUserRes(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy user");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressRes> getMyProgress(ProgressStatus status, Long topicId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return lessonProgressRepository.findAll(buildProgressSpec(userId, status, topicId),
                        Sort.by(Sort.Direction.DESC, "completedAt")).stream()
                .map(EntityMapper::toLessonProgressRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LessonProgressRes getMyProgressById(Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return lessonProgressRepository.findByIdAndUserId(id, userId)
                .map(EntityMapper::toLessonProgressRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiến độ"));
    }

    @Override
    @Transactional
    public void deleteMyProgress(Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        LessonProgress progress = lessonProgressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiến độ"));
        lessonProgressRepository.delete(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabularyRes> getMyVocabularies(VocabStatus status, Long topicId, String keyword) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return userVocabularyRepository.findAll(buildUserVocabularySpec(userId, status, topicId, keyword),
                        Sort.by(Sort.Direction.DESC, "learnedAt")).stream()
                .map(EntityMapper::toUserVocabularyRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserVocabularyRes getMyVocabularyById(Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return userVocabularyRepository.findByIdAndUserId(id, userId)
                .map(EntityMapper::toUserVocabularyRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy từ trong word bank"));
    }

    @Override
    @Transactional
    public UserVocabularyRes saveVocabulary(SaveUserVocabularyReq request) {
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();

        if (userVocabularyRepository.existsByUserIdAndVocabularyId(currentUser.getId(), request.getVocabularyId())) {
            throw new BadRequestException("Từ vựng đã có trong word bank");
        }

        Vocabulary vocabulary = vocabularyRepository.findById(request.getVocabularyId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy từ vựng"));

        User user = getUserEntity(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        UserVocabulary userVocabulary = new UserVocabulary();
        userVocabulary.setUser(user);
        userVocabulary.setVocabulary(vocabulary);
        userVocabulary.setLearnedAt(LocalDateTime.now());

        return EntityMapper.toUserVocabularyRes(userVocabularyRepository.save(userVocabulary));
    }

    @Override
    @Transactional
    public UserVocabularyRes updateVocabulary(Long id, UpdateUserVocabularyReq request) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        UserVocabulary userVocabulary = userVocabularyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy từ trong word bank"));
        userVocabulary.setStatus(request.getStatus());
        return EntityMapper.toUserVocabularyRes(userVocabularyRepository.save(userVocabulary));
    }

    @Override
    @Transactional
    public void deleteMyVocabulary(Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        UserVocabulary userVocabulary = userVocabularyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy từ trong word bank"));
        userVocabularyRepository.delete(userVocabulary);
    }

    private java.util.Optional<User> getUserEntity(Long id) {
        return userRepository.findById(id);
    }

    private Specification<User> buildUserSpec(String email, String name, Integer grade) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(email)) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (grade != null) {
                predicates.add(cb.equal(root.get("grade"), grade));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<LessonProgress> buildProgressSpec(Long userId, ProgressStatus status, Long topicId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (topicId != null) {
                Join<Object, Object> lesson = root.join("lesson");
                predicates.add(cb.equal(lesson.get("topic").get("id"), topicId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<UserVocabulary> buildUserVocabularySpec(
            Long userId, VocabStatus status, Long topicId, String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            Join<Object, Object> vocabulary = root.join("vocabulary");
            if (topicId != null) {
                predicates.add(cb.equal(vocabulary.get("topic").get("id"), topicId));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(vocabulary.get("word")), pattern),
                        cb.like(cb.lower(vocabulary.get("meaningVi")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
