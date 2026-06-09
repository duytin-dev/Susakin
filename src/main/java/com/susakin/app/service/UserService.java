package com.susakin.app.service;

import com.susakin.app.dto.req.user.UserAdminReq;
import com.susakin.app.dto.req.user.UserUpdateReq;
import com.susakin.app.dto.req.vocabulary.SaveUserVocabularyReq;
import com.susakin.app.dto.req.vocabulary.UpdateUserVocabularyReq;
import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.user.UserRes;
import com.susakin.app.dto.res.vocabulary.UserVocabularyRes;
import com.susakin.app.utils.enums.ProgressStatus;
import com.susakin.app.utils.enums.VocabStatus;

import java.util.List;

public interface UserService {

    UserRes getMe();

    UserRes updateMe(UserUpdateReq request);

    List<UserRes> getAll(String email, String name, Integer grade);

    UserRes getById(Long id);

    UserRes update(Long id, UserAdminReq request);

    void delete(Long id);

    List<LessonProgressRes> getMyProgress(ProgressStatus status, Long topicId);

    LessonProgressRes getMyProgressById(Long id);

    void deleteMyProgress(Long id);

    List<UserVocabularyRes> getMyVocabularies(VocabStatus status, Long topicId, String keyword);

    UserVocabularyRes getMyVocabularyById(Long id);

    UserVocabularyRes saveVocabulary(SaveUserVocabularyReq request);

    UserVocabularyRes updateVocabulary(Long id, UpdateUserVocabularyReq request);

    void deleteMyVocabulary(Long id);
}
