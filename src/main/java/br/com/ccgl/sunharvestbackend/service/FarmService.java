package br.com.ccgl.sunharvestbackend.service;

import br.com.ccgl.sunharvestbackend.domain.*;
import br.com.ccgl.sunharvestbackend.entity.*;
import br.com.ccgl.sunharvestbackend.exception.FarmDeletionException;
import br.com.ccgl.sunharvestbackend.exception.ResourceNotFoundException;
import br.com.ccgl.sunharvestbackend.repository.AlertRepository;
import br.com.ccgl.sunharvestbackend.repository.AlertSeverityRepository;
import br.com.ccgl.sunharvestbackend.repository.FarmRepository;
import br.com.ccgl.sunharvestbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmRepository farmRepository;
    private final AlertRepository alertRepository;
    private final AlertSeverityRepository alertSeverityRepository;
    private final UserRepository userRepository;
    private final NasaPowerService nasaPowerService;
    private final PenmanMonteithCalculator penmanMonteithCalculator;

    @Transactional
    public FarmResponse createFarm(FarmRequest request, String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        GeoLocation location = new GeoLocation(request.latitude(), request.longitude(), request.altitude());

        Farm farm = Farm.builder()
                .name(request.name())
                .location(location)
                .areaHectares(request.areaHectares())
                .user(user)
                .build();

        FarmCrop crop = FarmCrop.builder().cropType(request.cropType()).farm(farm).build();
        FarmSoil soil = FarmSoil.builder().soilType(request.soilType()).farm(farm).build();
        FarmIrrigation irrigation = FarmIrrigation.builder()
                .irrigationEfficiency(request.irrigationEfficiency()).farm(farm).build();

        farm.setCrop(crop);
        farm.setSoil(soil);
        farm.setIrrigation(irrigation);

        if (request.solarPanelCapacity() != null) {
            FarmSolarPanel panel = FarmSolarPanel.builder()
                    .farm(farm)
                    .solarPanelCapacity(request.solarPanelCapacity())
                    .pumpPower(request.pumpPower())
                    .tiltDegrees(request.tiltDegrees())
                    .azimuthDegrees(request.azimuthDegrees())
                    .performanceRatio(request.performanceRatio())
                    .build();
            farm.getSolarPanels().add(panel);
        }

        return toFarmResponse(farmRepository.save(farm));
    }

    public List<FarmResponse> getFarmsByUser(String userId) {
        return farmRepository.findByUserEmail(userId).stream()
                .map(this::toFarmResponse)
                .toList();
    }

    public FarmResponse getFarmById(Long id, String userId) {
        Farm farm = farmRepository.findByIdAndUserEmail(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda não encontrada: " + id));
        return toFarmResponse(farm);
    }

    @Transactional
    public FarmResponse updateFarm(Long id, FarmRequest request, String userId) {
        Farm farm = farmRepository.findByIdAndUserEmail(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda não encontrada: " + id));

        farm.setName(request.name());
        farm.setLocation(new GeoLocation(request.latitude(), request.longitude(), request.altitude()));
        farm.setAreaHectares(request.areaHectares());

        if (farm.getCrop() != null) farm.getCrop().setCropType(request.cropType());
        if (farm.getSoil() != null) farm.getSoil().setSoilType(request.soilType());
        if (farm.getIrrigation() != null) farm.getIrrigation().setIrrigationEfficiency(request.irrigationEfficiency());

        return toFarmResponse(farmRepository.save(farm));
    }

    @Transactional
    public void deleteFarm(Long id, String userId) {
        Farm farm = farmRepository.findByIdAndUserEmail(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda não encontrada: " + id));

        if (alertRepository.existsByFarmIdAndSeverityNameAndAcknowledged(id, "CRITICAL", "N")) {
            throw new FarmDeletionException(
                    "Fazenda possui alertas CRITICAL não reconhecidos. Reconheça-os antes de deletar.");
        }

        farmRepository.delete(farm);
    }

    public EtoResult calculateEto(FarmResponse farm) {
        NasaClimateData data = nasaPowerService.getClimateData(farm.latitude(), farm.longitude());
        double altitude = farm.altitude() != null ? farm.altitude().doubleValue() : 0.0;
        int doy = LocalDate.now().getDayOfYear();
        double eto = penmanMonteithCalculator.calculate(data, farm.latitude().doubleValue(), altitude, doy);
        String date = LocalDate.now().minusDays(1).toString();
        return new EtoResult(
                Math.round(eto * 100.0) / 100.0,
                farm.latitude(),
                farm.longitude(),
                date,
                String.format("ETo = %.2f mm/dia — FAO-56 Penman-Monteith com dados NASA POWER", eto));
    }

    private FarmResponse toFarmResponse(Farm farm) {
        var panels = farm.getSolarPanels();
        return new FarmResponse(
                farm.getId(),
                farm.getName(),
                farm.getLocation() != null ? farm.getLocation().getLatitude() : null,
                farm.getLocation() != null ? farm.getLocation().getLongitude() : null,
                farm.getLocation() != null ? farm.getLocation().getAltitude() : null,
                farm.getAreaHectares(),
                farm.getCrop() != null ? farm.getCrop().getCropType() : null,
                farm.getSoil() != null ? farm.getSoil().getSoilType() : null,
                farm.getIrrigation() != null ? farm.getIrrigation().getIrrigationEfficiency() : null,
                panels != null && !panels.isEmpty() ? panels.get(0).getSolarPanelCapacity() : null,
                panels != null && !panels.isEmpty() ? panels.get(0).getPerformanceRatio() : null,
                farm.getCreatedAt(),
                farm.getUpdatedAt());
    }
}
