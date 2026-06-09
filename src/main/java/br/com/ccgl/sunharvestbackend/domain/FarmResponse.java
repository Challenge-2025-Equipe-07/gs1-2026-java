package br.com.ccgl.sunharvestbackend.domain;

import java.math.BigDecimal;
import java.util.Date;

public record FarmResponse(
        Long id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal altitude,
        BigDecimal areaHectares,
        String cropType,
        String soilType,
        BigDecimal irrigationEfficiency,
        BigDecimal solarPanelCapacity,
        BigDecimal performanceRatio,
        Date createdAt,
        Date updatedAt
) {}
