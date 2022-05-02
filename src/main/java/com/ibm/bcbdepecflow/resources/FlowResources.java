package com.ibm.bcbdepecflow.resources;

import com.ibm.bcbdepecflow.entities.Flow;
import com.ibm.bcbdepecflow.entities.FlowSum;
import com.ibm.bcbdepecflow.services.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FlowResources {

    @Autowired
    private FlowService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Flow> findById(@PathVariable Long id) {
        Flow obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Flow>> findByDate(
            @RequestParam(required = false) LocalDate dataInicial,
            @RequestParam(required = false) LocalDate dataFinal) {
        List<Flow> list = new ArrayList<>();
        if (dataInicial != null && dataFinal != null) {
            list = service.findAllByDataBetween(dataInicial, dataFinal);
        } else if (dataInicial != null) {
            list = service.findAllByDataGreaterThanEqual(dataInicial);
        } else if (dataFinal != null) {
            list = service.findAllByDataLessThanEqual(dataFinal);
        } else {
            list = service.findAll();
        }
        return ResponseEntity.ok().body(list);
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

    @PostMapping
    public ResponseEntity<Flow> insert(@RequestBody Flow obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Flow> update(@PathVariable Long id, @RequestBody Flow obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Flow> updatePartially(@PathVariable Long id, @RequestBody Flow obj) {
        obj = service.updatePartially(id, obj);
        return ResponseEntity.ok().body(obj);
    }
}
