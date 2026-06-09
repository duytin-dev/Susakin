package com.susakin.app.dto.req.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicReq {

    @NotBlank(message = "Name is required")
    private String name;

    private String thumbnailUrl;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;
}
