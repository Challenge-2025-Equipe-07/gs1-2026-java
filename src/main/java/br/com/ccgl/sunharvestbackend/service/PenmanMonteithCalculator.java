package br.com.ccgl.sunharvestbackend.service;

import br.com.ccgl.sunharvestbackend.domain.NasaClimateData;
import org.springframework.stereotype.Component;

/**
 * FAO-56 Penman-Monteith reference evapotranspiration (ETo) in mm/day.
 */
@Component
public class PenmanMonteithCalculator {

    public double calculate(NasaClimateData data, double latitudeDeg, double altitudeM, int dayOfYear) {
        double tMax = data.tMax();
        double tMin = data.tMin();
        double T = (tMax + tMin) / 2.0;
        double u2 = data.windSpeed();
        double Rs = data.solarRadiation();
        double RH = data.rhMean();

        // Saturation vapour pressure [kPa]
        double eTmax = 0.6108 * Math.exp(17.27 * tMax / (tMax + 237.3));
        double eTmin = 0.6108 * Math.exp(17.27 * tMin / (tMin + 237.3));
        double es = (eTmax + eTmin) / 2.0;
        double ea = es * RH / 100.0;

        // Slope of saturation vapour pressure curve [kPa/°C]
        double delta = 4098.0 * (0.6108 * Math.exp(17.27 * T / (T + 237.3))) / Math.pow(T + 237.3, 2);

        // Atmospheric pressure [kPa]
        double P = 101.3 * Math.pow((293.0 - 0.0065 * altitudeM) / 293.0, 5.26);

        // Psychrometric constant [kPa/°C]
        double gamma = 0.000665 * P;

        // Extraterrestrial radiation [MJ/m²/day]
        double latRad = Math.toRadians(latitudeDeg);
        double dr = 1.0 + 0.033 * Math.cos(2.0 * Math.PI * dayOfYear / 365.0);
        double decl = 0.409 * Math.sin(2.0 * Math.PI * dayOfYear / 365.0 - 1.39);
        double ws = Math.acos(-Math.tan(latRad) * Math.tan(decl));
        double Ra = (24.0 / Math.PI) * 4.92 * dr
                * (ws * Math.sin(latRad) * Math.sin(decl) + Math.cos(latRad) * Math.cos(decl) * Math.sin(ws));

        // Clear-sky solar radiation [MJ/m²/day]
        double Rso = (0.75 + 2e-5 * altitudeM) * Ra;

        // Net shortwave radiation [MJ/m²/day]
        double Rns = 0.77 * Rs;

        // Net longwave radiation [MJ/m²/day]
        double f = (Rso > 0) ? Math.min(Rs / Rso, 1.0) : 0.8;
        double tMaxK4 = Math.pow(tMax + 273.16, 4);
        double tMinK4 = Math.pow(tMin + 273.16, 4);
        double Rnl = 4.903e-9 * (tMaxK4 + tMinK4) / 2.0
                * (0.34 - 0.14 * Math.sqrt(Math.max(ea, 0)))
                * (1.35 * f - 0.35);

        double Rn = Rns - Rnl;

        // ETo [mm/day]
        double numerator = 0.408 * delta * Rn + gamma * (900.0 / (T + 273.0)) * u2 * (es - ea);
        double denominator = delta + gamma * (1.0 + 0.34 * u2);

        return (denominator != 0) ? numerator / denominator : 0.0;
    }
}
