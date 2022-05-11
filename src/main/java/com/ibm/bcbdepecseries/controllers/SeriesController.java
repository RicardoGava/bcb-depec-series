package com.ibm.bcbdepecseries.controllers;

import com.ibm.bcbdepecseries.domain.Series;
import com.ibm.bcbdepecseries.domain.SeriesSum;
import com.ibm.bcbdepecseries.services.SeriesService;
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
public class SeriesController {

    @Autowired
    private SeriesService service;

    @ApiOperation(value = "Retorna o valor do ID desejado")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Series> findById(@PathVariable Long id) {
        Series obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @ApiOperation(value = "Retorna valores com o filtro de dia, mês e ano")
    @GetMapping(value = "/data")
    @ResponseBody
    public ResponseEntity<List<Series>> findByData(
            @ApiParam(value = "Filtrar por dia. Formato: dd")
            @RequestParam(required = false) Integer dia,
            @ApiParam(value = "Filtrar por mês. Formato: MM")
            @RequestParam(required = false) Integer mes,
            @ApiParam(value = "Filtrar por ano. Formato: yyyy")
            @RequestParam(required = false) Integer ano
    ) {
        List<Series> list = service.findByData(dia, mes, ano);
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "Retorna valores paginados com parâmetros de" +
            " paginação (page, size, sort), data inicial e data final")
    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<Series>> multipleFindPageable(
            Pageable pageable,
            @ApiParam(value = "Filtrar por data inicial. Formato: dd/MM/yyyy")
            @RequestParam(required = false) LocalDate dataInicial,
            @ApiParam(value = "Filtrar por data final. Formato: dd/MM/yyyy")
            @RequestParam(required = false) LocalDate dataFinal) {
        Page<Series> page;
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
    public ResponseEntity<List<Series>> findLastValues(@PathVariable Integer valores) {
        List<Series> list = service.findLastValues(valores);
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "Retorna a soma de todos os anos ou de algum ano especificado como parâmetro")
    @GetMapping(value = "/soma")
    @ResponseBody
    public ResponseEntity<List<SeriesSum>> getYearTotal(
            @RequestParam(required = false) String ano) {
        List<SeriesSum> list = new ArrayList<>();
        list = service.getYearTotal(ano);
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "Insere um valor no repositório")
    @PostMapping(value = "/insert")
    public ResponseEntity<Series> insert(@RequestBody Series obj) {
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
    public ResponseEntity<Series> update(@PathVariable Long id, @RequestBody Series obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @ApiOperation(value = "Edita uma coluna de um determinado ID no repositório")
    @PatchMapping(value = "/update/{id}")
    public ResponseEntity<Series> updatePartially(@PathVariable Long id, @RequestBody Series obj) {
        obj = service.updatePartially(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
