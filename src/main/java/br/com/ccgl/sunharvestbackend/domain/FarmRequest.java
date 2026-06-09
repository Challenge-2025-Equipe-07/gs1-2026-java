package br.com.ccgl.sunharvestbackend.domain;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record FarmRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude,
        BigDecimal altitude,
        @NotNull @DecimalMin("0") BigDecimal areaHectares,
        @NotBlank @Size(max = 100) String cropType,
        @NotBlank @Size(max = 100) String soilType,
        @NotNull @DecimalMin("0") @DecimalMax("1") BigDecimal irrigationEfficiency,
        BigDecimal solarPanelCapacity,
        BigDecimal pumpPower,
        BigDecimal tiltDegrees,
        BigDecimal azimuthDegrees,
        BigDecimal performanceRatio
) {}
