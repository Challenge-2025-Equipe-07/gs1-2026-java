package br.com.ccgl.sunharvestbackend.domain;

import java.util.Date;

public record AlertResponse(
        Long id,
        String message,
        String acknowledged,
        String severity,
        Long farmId,
        Date createdAt
) {}
