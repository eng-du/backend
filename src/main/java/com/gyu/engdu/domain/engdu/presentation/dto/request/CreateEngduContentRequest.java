package com.gyu.engdu.domain.engdu.presentation.dto.request;

import com.gyu.engdu.domain.engdu.domain.enums.EngduCreationStep;
import jakarta.validation.constraints.NotNull;

public record CreateEngduContentRequest(
    @NotNull
    EngduCreationStep step
) {

}
