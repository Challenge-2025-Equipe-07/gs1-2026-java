package br.com.ccgl.sunharvestbackend.domain;

public record NasaClimateData(
        double solarRadiation,
        double tMax,
        double tMin,
        double rhMean,
        double windSpeed
) {}
