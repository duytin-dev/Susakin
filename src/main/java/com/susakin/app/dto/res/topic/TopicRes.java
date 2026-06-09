package com.susakin.app.dto.res.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TopicRes {

    private Long id;
    private String name;
    private String thumbnailUrl;
    private Integer orderIndex;
}
