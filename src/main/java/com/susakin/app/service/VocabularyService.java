package com.susakin.app.service;

import com.susakin.app.dto.req.vocabulary.VocabularyReq;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;

import java.util.List;

public interface VocabularyService {

    List<VocabularyRes> getAll(Long topicId, String keyword);

    VocabularyRes getById(Long id);

    VocabularyRes create(VocabularyReq request);

    VocabularyRes update(Long id, VocabularyReq request);

    void delete(Long id);
}
