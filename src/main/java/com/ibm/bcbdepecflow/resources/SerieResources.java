package com.ibm.bcbdepecflow.resources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping(value = "/serie")
public class SerieResources {

    @Value("${api-bcb-serie}")
    String serie;

    @GetMapping
    public ResponseEntity<String> serie() {
        return ResponseEntity.ok().body(serie);
    }

}
