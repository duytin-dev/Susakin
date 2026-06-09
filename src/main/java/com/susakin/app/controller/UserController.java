package com.susakin.app.controller;

import com.susakin.app.dto.req.user.UserAdminReq;
import com.susakin.app.dto.req.user.UserUpdateReq;
import com.susakin.app.dto.req.vocabulary.SaveUserVocabularyReq;
import com.susakin.app.dto.req.vocabulary.UpdateUserVocabularyReq;
import com.susakin.app.dto.res.common.ApiResponse;
import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.user.UserRes;
import com.susakin.app.dto.res.vocabulary.UserVocabularyRes;
import com.susakin.app.service.UserService;
import com.susakin.app.utils.enums.ProgressStatus;
import com.susakin.app.utils.enums.VocabStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserRes> getMe() {
        return ApiResponse.ok(userService.getMe());
    }

    @PutMapping("/me")
    public ApiResponse<UserRes> updateMe(@Valid @RequestBody UserUpdateReq request) {
        return ApiResponse.ok("Cập nhật profile thành công", userService.updateMe(request));
    }

    @GetMapping
    public ApiResponse<List<UserRes>> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer grade
    ) {
        return ApiResponse.ok(userService.getAll(email, name, grade));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserRes> getById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserRes> update(@PathVariable Long id, @Valid @RequestBody UserAdminReq request) {
        return ApiResponse.ok("Cập nhật user thành công", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok("Xóa user thành công");
    }

    @GetMapping("/me/progress")
    public ApiResponse<List<LessonProgressRes>> getMyProgress(
            @RequestParam(required = false) ProgressStatus status,
            @RequestParam(required = false) Long topicId
    ) {
        return ApiResponse.ok(userService.getMyProgress(status, topicId));
    }

    @GetMapping("/me/progress/{id}")
    public ApiResponse<LessonProgressRes> getMyProgressById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getMyProgressById(id));
    }

    @DeleteMapping("/me/progress/{id}")
    public ApiResponse<Void> deleteMyProgress(@PathVariable Long id) {
        userService.deleteMyProgress(id);
        return ApiResponse.ok("Xóa tiến độ thành công");
    }

    @GetMapping("/me/vocabularies")
    public ApiResponse<List<UserVocabularyRes>> getMyVocabularies(
            @RequestParam(required = false) VocabStatus status,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(userService.getMyVocabularies(status, topicId, keyword));
    }

    @GetMapping("/me/vocabularies/{id}")
    public ApiResponse<UserVocabularyRes> getMyVocabularyById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getMyVocabularyById(id));
    }

    @PostMapping("/me/vocabularies")
    public ApiResponse<UserVocabularyRes> saveVocabulary(@Valid @RequestBody SaveUserVocabularyReq request) {
        return ApiResponse.ok("Lưu từ vào word bank thành công", userService.saveVocabulary(request));
    }

    @PutMapping("/me/vocabularies/{id}")
    public ApiResponse<UserVocabularyRes> updateVocabulary(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserVocabularyReq request
    ) {
        return ApiResponse.ok("Cập nhật trạng thái từ thành công", userService.updateVocabulary(id, request));
    }

    @DeleteMapping("/me/vocabularies/{id}")
    public ApiResponse<Void> deleteMyVocabulary(@PathVariable Long id) {
        userService.deleteMyVocabulary(id);
        return ApiResponse.ok("Xóa từ khỏi word bank thành công");
    }
}
