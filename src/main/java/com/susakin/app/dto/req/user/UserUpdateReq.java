package com.susakin.app.dto.req.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateReq {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be between 1 and 5")
    @Max(value = 5, message = "Grade must be between 1 and 5")
    private Integer grade;
}
