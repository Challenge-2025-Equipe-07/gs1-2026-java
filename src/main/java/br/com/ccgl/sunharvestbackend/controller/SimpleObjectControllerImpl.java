package br.com.ccgl.sunharvestbackend.controller;

import br.com.ccgl.sunharvestbackend.domain.SimpleObjectRequest;
import br.com.ccgl.sunharvestbackend.entity.SimpleObject;
import br.com.ccgl.sunharvestbackend.repository.SimpleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(SimpleObjectControllerImpl.PATH_ROOT)
public class SimpleObjectControllerImpl {

    public static final String PATH_ROOT = "/simple-object";

    @Autowired
    private SimpleRepository simpleRepository;

    @GetMapping("/{id}")
    public SimpleObject getById(@PathVariable("id") Long id) {
        return simpleRepository.getSimpleObjectById(id);
    }

    @PostMapping
    public SimpleObject postObject(@RequestBody SimpleObjectRequest requestObject) {
        SimpleObject object = new SimpleObject();
        object.setName(requestObject.getName());
        return simpleRepository.save(object);
    }
}
