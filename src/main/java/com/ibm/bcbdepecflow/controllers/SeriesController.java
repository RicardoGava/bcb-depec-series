package com.ibm.bcbdepecflow.controllers;

import com.ibm.bcbdepecflow.services.SeriesMetadataHashMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;


@RestController
@CrossOrigin
@RequestMapping(value = "/serie")
public class SeriesController {

    @Autowired
    private SeriesMetadataHashMapService seriesMetadata;

    @GetMapping
    public ResponseEntity<HashMap<String, String>> seriesMetadata() {
        return ResponseEntity.ok().body(seriesMetadata.getSeriesMetadataHashMap());
    }

}
