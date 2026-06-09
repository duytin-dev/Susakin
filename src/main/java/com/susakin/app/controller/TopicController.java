package com.susakin.app.controller;

import com.susakin.app.dto.req.topic.TopicReq;
import com.susakin.app.dto.res.common.ApiResponse;
import com.susakin.app.dto.res.lesson.LessonRes;
import com.susakin.app.dto.res.topic.TopicRes;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;
import com.susakin.app.service.TopicService;
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
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ApiResponse<List<TopicRes>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer orderIndex
    ) {
        return ApiResponse.ok(topicService.getAll(name, orderIndex));
    }

    @GetMapping("/{id}")
    public ApiResponse<TopicRes> getById(@PathVariable Long id) {
        return ApiResponse.ok(topicService.getById(id));
    }

    @PostMapping
    public ApiResponse<TopicRes> create(@Valid @RequestBody TopicReq request) {
        return ApiResponse.ok("Tạo topic thành công", topicService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TopicRes> update(@PathVariable Long id, @Valid @RequestBody TopicReq request) {
        return ApiResponse.ok("Cập nhật topic thành công", topicService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        topicService.delete(id);
        return ApiResponse.ok("Xóa topic thành công");
    }

    @GetMapping("/{id}/vocabularies")
    public ApiResponse<List<VocabularyRes>> getVocabulariesByTopic(
            @PathVariable Long id,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(topicService.getVocabulariesByTopic(id, keyword));
    }

    @GetMapping("/{id}/lessons")
    public ApiResponse<List<LessonRes>> getLessonsByTopic(
            @PathVariable Long id,
            @RequestParam(required = false) String type
    ) {
        return ApiResponse.ok(topicService.getLessonsByTopic(id, type));
    }
}
