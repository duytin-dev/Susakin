package com.susakin.app.service.impl;

import com.susakin.app.dto.req.vocabulary.VocabularyReq;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;
import com.susakin.app.entity.Topic;
import com.susakin.app.entity.Vocabulary;
import com.susakin.app.exception.ResourceNotFoundException;
import com.susakin.app.mapper.EntityMapper;
import com.susakin.app.repository.TopicRepository;
import com.susakin.app.repository.UserVocabularyRepository;
import com.susakin.app.repository.VocabularyRepository;
import com.susakin.app.service.VocabularyService;
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
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final TopicRepository topicRepository;
    private final UserVocabularyRepository userVocabularyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VocabularyRes> getAll(Long topicId, String keyword) {
        return vocabularyRepository.findAll(buildSpec(topicId, keyword), Sort.by("orderIndex")).stream()
                .map(EntityMapper::toVocabularyRes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VocabularyRes getById(Long id) {
        return vocabularyRepository.findById(id)
                .map(EntityMapper::toVocabularyRes)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy từ vựng"));
    }

    @Override
    @Transactional
    public VocabularyRes create(VocabularyReq request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));

        Vocabulary vocabulary = new Vocabulary();
        applyFields(vocabulary, request, topic);
        return EntityMapper.toVocabularyRes(vocabularyRepository.save(vocabulary));
    }

    @Override
    @Transactional
    public VocabularyRes update(Long id, VocabularyReq request) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy từ vựng"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy topic"));

        applyFields(vocabulary, request, topic);
        return EntityMapper.toVocabularyRes(vocabularyRepository.save(vocabulary));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!vocabularyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy từ vựng");
        }
        userVocabularyRepository.deleteByVocabularyId(id);
        vocabularyRepository.deleteById(id);
    }

    private void applyFields(Vocabulary vocabulary, VocabularyReq request, Topic topic) {
        vocabulary.setTopic(topic);
        vocabulary.setWord(request.getWord());
        vocabulary.setMeaningVi(request.getMeaningVi());
        vocabulary.setImageUrl(request.getImageUrl());
        vocabulary.setAudioUrl(request.getAudioUrl());
        vocabulary.setOrderIndex(request.getOrderIndex());
    }

    private Specification<Vocabulary> buildSpec(Long topicId, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            }
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
}
