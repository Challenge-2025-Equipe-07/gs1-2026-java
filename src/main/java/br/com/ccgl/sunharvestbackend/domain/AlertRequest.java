package br.com.ccgl.sunharvestbackend.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertRequest(
        @NotBlank @Size(max = 500) String message,
        @NotNull Long severityId
) {}
