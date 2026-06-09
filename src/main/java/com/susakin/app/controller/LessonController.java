package com.susakin.app.controller;

import com.susakin.app.dto.req.lesson.LessonProgressReq;
import com.susakin.app.dto.req.lesson.LessonReq;
import com.susakin.app.dto.res.common.ApiResponse;
import com.susakin.app.dto.res.lesson.LessonProgressRes;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.service.LessonService;
import com.susakin.app.utils.enums.LessonType;
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
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ApiResponse<List<LessonRes>> getAll(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) LessonType type,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(lessonService.getAll(topicId, type, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<LessonRes> getById(@PathVariable Long id) {
        return ApiResponse.ok(lessonService.getById(id));
    }

    @PostMapping
    public ApiResponse<LessonRes> create(@Valid @RequestBody LessonReq request) {
        return ApiResponse.ok("Tạo bài học thành công", lessonService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<LessonRes> update(@PathVariable Long id, @Valid @RequestBody LessonReq request) {
        return ApiResponse.ok("Cập nhật bài học thành công", lessonService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ApiResponse.ok("Xóa bài học thành công");
    }

    @PostMapping("/{id}/progress")
    public ApiResponse<LessonProgressRes> saveProgress(
            @PathVariable Long id,
            @Valid @RequestBody LessonProgressReq request
    ) {
        return ApiResponse.ok("Lưu tiến độ thành công", lessonService.saveProgress(id, request));
    }
}
