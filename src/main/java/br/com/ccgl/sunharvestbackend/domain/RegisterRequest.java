package br.com.ccgl.sunharvestbackend.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String displayName,
        @Email @NotBlank @Size(max = 100) String email,
        @NotBlank @Size(min = 6, max = 100) String password
) {}
