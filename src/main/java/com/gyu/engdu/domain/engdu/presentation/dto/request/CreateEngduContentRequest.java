package com.gyu.engdu.domain.engdu.presentation.dto.request;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import jakarta.validation.constraints.NotNull;

public record CreateEngduContentRequest(
        @NotNull PartType step) {

}
