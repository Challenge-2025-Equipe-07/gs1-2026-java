package br.com.ccgl.sunharvestbackend.controller;

import br.com.ccgl.sunharvestbackend.domain.AlertResponse;
import br.com.ccgl.sunharvestbackend.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AlertResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toModel(alertService.getAlertById(id)));
    }

    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<EntityModel<AlertResponse>> acknowledge(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean acknowledge) {
        return ResponseEntity.ok(toModel(alertService.acknowledgeAlert(id, acknowledge)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<AlertResponse> toModel(AlertResponse r) {
        return EntityModel.of(r,
                linkTo(methodOn(AlertController.class).findById(r.id())).withSelfRel(),
                linkTo(methodOn(FarmController.class).findById(r.farmId(), null)).withRel("farm"));
    }
}
