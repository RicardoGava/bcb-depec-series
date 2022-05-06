package com.ibm.bcbdepecflow.controllers;

import com.ibm.bcbdepecflow.domain.Flow;
import com.ibm.bcbdepecflow.domain.FlowSum;
import com.ibm.bcbdepecflow.services.FlowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @ApiOperation(value = "Retorna o valor do ID desejado")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Flow> findById(@PathVariable Long id) {
        Flow obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @ApiOperation(value = "Retorna valores com o filtro de dia, mês e ano")
    @GetMapping(value= "/data")
    @ResponseBody
    public ResponseEntity<List<Flow>> findByData(
            @ApiParam(value = "Filtrar por dia. Formato: dd")
            @RequestParam(required = false) Integer dia,
            @ApiParam(value = "Filtrar por mês. Formato: MM")
            @RequestParam(required = false) Integer mes,
            @ApiParam(value = "Filtrar por ano. Formato: yyyy")
            @RequestParam(required = false) Integer ano
            ) {
        List<Flow> list = service.findByData(dia, mes, ano);
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "Retorna valores paginados com parâmetros de" +
            " paginação (page, size, sort), data inicial e data final")
    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Flow>> multipleFindPageable(
            Pageable pageable,
            @ApiParam(value = "Filtrar por data inicial. Formato: dd/MM/yyyy")
            @RequestParam(required = false) LocalDate dataInicial,
            @ApiParam(value = "Filtrar por data final. Formato: dd/MM/yyyy")
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

    @ApiOperation(value = "Retorna os últimos valores ordenados por data")
    @GetMapping(value = "/ultimos/{valores}")
    public ResponseEntity<List<Flow>> findLastValues(@PathVariable Integer valores) {
        List<Flow> list = service.findLastValues(valores);
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "Retorna a soma de todos os anos ou de algum ano especificado como parâmetro")
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

    @ApiOperation(value = "Insere um valor no repositório")
    @PostMapping(value = "/insert")
    public ResponseEntity<Flow> insert(@RequestBody Flow obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @ApiOperation(value = "Deleta um determinado ID do repositório")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Edita um determinado ID no repositório")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Flow> update(@PathVariable Long id, @RequestBody Flow obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @ApiOperation(value = "Edita uma coluna de um determinado ID no repositório")
    @PatchMapping(value = "/update/{id}")
    public ResponseEntity<Flow> updatePartially(@PathVariable Long id, @RequestBody Flow obj) {
        obj = service.updatePartially(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
