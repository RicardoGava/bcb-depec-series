package com.ibm.bcbdepecflow.controllers;

import com.ibm.bcbdepecflow.domain.Flow;
import com.ibm.bcbdepecflow.domain.FlowSum;
import com.ibm.bcbdepecflow.services.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "${api-bcb-serie}/dados")
public class FlowController {

    @Autowired
    private FlowService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Flow> findById(@PathVariable Long id) {
        Flow obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping(value= "/data")
    @ResponseBody
    public ResponseEntity<List<Flow>> findByDataLike(
            @RequestParam(required = false) Integer dia,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano
            ) {
        List<Flow> list = service.findByData(dia, mes, ano);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Flow>> multipleFind(
            Pageable pageable,
            @RequestParam(required = false) LocalDate dataInicial,
            @RequestParam(required = false) LocalDate dataFinal) {
        Page<Flow> page;
        if (dataInicial != null && dataFinal != null) {
            page = service.findAllByDataBetween(dataInicial, dataFinal, pageable);
        } else if (dataInicial != null) {
            page = service.findAllByDataGreaterThanEqual(dataInicial, pageable);
        } else if (dataFinal != null) {
            page = service.findAllByDataLessThanEqual(dataFinal, pageable);
        } else {
            page = service.findAll(pageable);
        }
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/ultimos/{valores}")
    public ResponseEntity<List<Flow>> findLastValues(@PathVariable Integer valores) {
        List<Flow> list = service.findLastValues(valores);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/soma")
    @ResponseBody
    public ResponseEntity<List<FlowSum>> getYearTotal(
            @RequestParam(required = false) String ano) {
        List<FlowSum> list = new ArrayList<>();
        if (ano != null) {
            list = service.getYearTotal(ano);
        } else {
            list = service.getYearTotal(null);
        }
        return ResponseEntity.ok().body(list);
    }

    @PostMapping(value = "/edit")
    public ResponseEntity<Flow> insert(@RequestBody Flow obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping(value = "/edit/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<Flow> update(@PathVariable Long id, @RequestBody Flow obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @PatchMapping(value = "/edit/{id}")
    public ResponseEntity<Flow> updatePartially(@PathVariable Long id, @RequestBody Flow obj) {
        obj = service.updatePartially(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
