package com.ibm.bcbdepecseries.controllers;

import com.ibm.bcbdepecseries.services.SeriesMetadataHashMapService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;


@RestController
@RequestMapping(value = "/serie")
public class SeriesMetadadosController {

    @Autowired
    private SeriesMetadataHashMapService seriesMetadata;

    @ApiOperation(value = "Retorna os metadados das séries")
    @GetMapping
    public ResponseEntity<HashMap<String, String>> seriesMetadata() {
        return ResponseEntity.ok().body(seriesMetadata.getSeriesMetadataHashMap());
    }

}
