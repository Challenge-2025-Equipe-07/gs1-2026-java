package br.com.ccgl.sunharvestbackend.domain;

import java.math.BigDecimal;

public record EtoResult(
        double eto,
        BigDecimal latitude,
        BigDecimal longitude,
        String dataDate,
        String description
) {}
