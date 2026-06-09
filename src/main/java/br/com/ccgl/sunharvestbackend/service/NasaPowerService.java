package br.com.ccgl.sunharvestbackend.service;

import br.com.ccgl.sunharvestbackend.domain.NasaClimateData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class NasaPowerService {

    private final RestClient restClient;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public NasaPowerService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://power.larc.nasa.gov")
                .build();
    }

    public NasaClimateData getClimateData(BigDecimal latitude, BigDecimal longitude) {
        String date = LocalDate.now().minusDays(2).format(FMT);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/temporal/daily/point")
                        .queryParam("parameters", "ALLSKY_SFC_SW_DWN,T2M_MAX,T2M_MIN,RH2M,WS2M")
                        .queryParam("community", "AG")
                        .queryParam("longitude", longitude.toPlainString())
                        .queryParam("latitude", latitude.toPlainString())
                        .queryParam("start", date)
                        .queryParam("end", date)
                        .queryParam("format", "JSON")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) response.get("properties");
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> parameters = (Map<String, Map<String, Object>>) properties.get("parameter");

        return new NasaClimateData(
                extractFirst(parameters.get("ALLSKY_SFC_SW_DWN")),
                extractFirst(parameters.get("T2M_MAX")),
                extractFirst(parameters.get("T2M_MIN")),
                extractFirst(parameters.get("RH2M")),
                extractFirst(parameters.get("WS2M")));
    }

    private double extractFirst(Map<String, Object> data) {
        if (data == null || data.isEmpty()) return 0.0;
        Object value = data.values().iterator().next();
        return value instanceof Number n ? n.doubleValue() : 0.0;
    }
}
