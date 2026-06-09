package com.susakin.app.dto.res.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserRes {

    private Long id;
    private String email;
    private String name;
    private Integer grade;
    private LocalDateTime createdAt;
}
