package br.com.ccgl.sunharvestbackend.controller;

import br.com.ccgl.sunharvestbackend.domain.*;
import br.com.ccgl.sunharvestbackend.service.AlertService;
import br.com.ccgl.sunharvestbackend.service.FarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;
    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<EntityModel<FarmResponse>> create(
            @Valid @RequestBody FarmRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        FarmResponse response = farmService.createFarm(request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<FarmResponse>>> list(
            @AuthenticationPrincipal UserDetails principal) {
        List<EntityModel<FarmResponse>> farms = farmService.getFarmsByUser(principal.getUsername())
                .stream().map(this::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(farms,
                linkTo(methodOn(FarmController.class).list(null)).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FarmResponse>> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toModel(farmService.getFarmById(id, principal.getUsername())));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<FarmResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FarmRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toModel(farmService.updateFarm(id, request, principal.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        farmService.deleteFarm(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/alerts")
    public ResponseEntity<CollectionModel<EntityModel<AlertResponse>>> listAlerts(
            @PathVariable Long id,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String acknowledged,
            @AuthenticationPrincipal UserDetails principal) {
        farmService.getFarmById(id, principal.getUsername());
        List<EntityModel<AlertResponse>> alerts = alertService.getAlertsByFarm(id, severity, acknowledged)
                .stream().map(this::toAlertModel).toList();
        return ResponseEntity.ok(CollectionModel.of(alerts,
                linkTo(methodOn(FarmController.class).listAlerts(id, severity, acknowledged, null)).withSelfRel()));
    }

    @PostMapping("/{id}/alerts")
    public ResponseEntity<EntityModel<AlertResponse>> createAlert(
            @PathVariable Long id,
            @Valid @RequestBody AlertRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        farmService.getFarmById(id, principal.getUsername());
        AlertResponse response = alertService.createAlert(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAlertModel(response));
    }

    @GetMapping("/{id}/eto")
    public ResponseEntity<EtoResult> calculateEto(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        FarmResponse farm = farmService.getFarmById(id, principal.getUsername());
        return ResponseEntity.ok(farmService.calculateEto(farm));
    }

    private EntityModel<FarmResponse> toModel(FarmResponse r) {
        return EntityModel.of(r,
                linkTo(methodOn(FarmController.class).findById(r.id(), null)).withSelfRel(),
                linkTo(methodOn(FarmController.class).listAlerts(r.id(), null, null, null)).withRel("alerts"),
                linkTo(methodOn(FarmController.class).calculateEto(r.id(), null)).withRel("eto"));
    }

    private EntityModel<AlertResponse> toAlertModel(AlertResponse r) {
        return EntityModel.of(r,
                linkTo(methodOn(AlertController.class).findById(r.id())).withSelfRel(),
                linkTo(methodOn(FarmController.class).findById(r.farmId(), null)).withRel("farm"));
    }
}
