package br.com.ccgl.sunharvestbackend.controller;

import br.com.ccgl.sunharvestbackend.domain.SimpleObjectRequest;
import br.com.ccgl.sunharvestbackend.entity.SimpleObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public interface SimpleObjectController {

    @GetMapping
    SimpleObject getById(Long id);

    @PostMapping
    void postObject(SimpleObjectRequest requestObject);
}
