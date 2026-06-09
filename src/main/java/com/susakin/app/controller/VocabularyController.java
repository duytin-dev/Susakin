package com.susakin.app.controller;

import com.susakin.app.dto.req.vocabulary.VocabularyReq;
import com.susakin.app.dto.res.common.ApiResponse;
import com.susakin.app.dto.res.vocabulary.VocabularyRes;
import com.susakin.app.service.VocabularyService;
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
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @GetMapping
    public ApiResponse<List<VocabularyRes>> getAll(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(vocabularyService.getAll(topicId, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<VocabularyRes> getById(@PathVariable Long id) {
        return ApiResponse.ok(vocabularyService.getById(id));
    }

    @PostMapping
    public ApiResponse<VocabularyRes> create(@Valid @RequestBody VocabularyReq request) {
        return ApiResponse.ok("Tạo từ vựng thành công", vocabularyService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VocabularyRes> update(@PathVariable Long id, @Valid @RequestBody VocabularyReq request) {
        return ApiResponse.ok("Cập nhật từ vựng thành công", vocabularyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        vocabularyService.delete(id);
        return ApiResponse.ok("Xóa từ vựng thành công");
    }
}
