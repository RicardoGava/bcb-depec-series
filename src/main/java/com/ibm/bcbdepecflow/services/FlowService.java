package com.ibm.bcbdepecflow.services;

import com.ibm.bcbdepecflow.domain.Flow;
import com.ibm.bcbdepecflow.domain.FlowSum;
import com.ibm.bcbdepecflow.repositories.FlowRepository;
import com.ibm.bcbdepecflow.services.exceptions.DataBaseException;
import com.ibm.bcbdepecflow.services.exceptions.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlowService {

    @Autowired
    private FlowRepository repository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Flow> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Flow findById(Long id) {
        Optional<Flow> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ItemNotFoundException(id));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Flow> findAllByDataBetween(LocalDate dataInicial, LocalDate datafinal) {
        return repository.findAllByDataBetween(dataInicial, datafinal);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Flow> findAllByDataGreaterThanEqual(LocalDate dataInicial) {
        return repository.findAllByDataGreaterThanEqual(dataInicial);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Flow> findAllByDataLessThanEqual(LocalDate datafinal) {
        return repository.findAllByDataLessThanEqual(datafinal);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Flow> findLastValues(Integer valores) {
        Page<Flow> page = repository.findAll(
                PageRequest.of(0, valores, Sort.by(
                        Sort.Direction.DESC, "data")));
        List<Flow> list = page.getContent();
        List<Flow> reversedList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            reversedList.add(list.get(i));
        }
        return reversedList;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<FlowSum> getYearTotal(String ano) {
        DateTimeFormatter yof = DateTimeFormatter.ofPattern("yyyy");
        List<Flow> list = repository.findAll();
        Map<String, List<Flow>> groupedByYear = list.stream()
                .collect(Collectors.groupingBy(flow -> flow.getData().format(yof)));
        List<FlowSum> sumList = new ArrayList<>();
        if (ano != null) {
            if (groupedByYear.containsKey(ano)) {
                sumList.add(genSumList(ano, groupedByYear));
            }
        } else {
            for (String key : groupedByYear.keySet()) {
                sumList.add(genSumList(key, groupedByYear));
            }
        }
        return sumList.stream()
                .sorted(Comparator.comparing(FlowSum::getAno))
                .collect(Collectors.toList());
    }

    public static FlowSum genSumList(String key, Map<String, List<Flow>> map) {
        List<Flow> flowList = map.get(key);
        FlowSum flowSum = new FlowSum(
                Integer.valueOf(key),
                flowList.size(),
                new BigDecimal(0)
        );
        for (Flow flow : flowList) {
            flowSum.setTotal(flowSum.getTotal().add(flow.getValor()));
        }
        return flowSum;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Flow insert(Flow obj) {
        return repository.save(obj);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Flow update(Long id, Flow obj) {
        try {
            Flow entity = repository.getById(id);
            entity.setData(obj.getData());
            entity.setValor(obj.getValor());
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Flow updatePartially(Long id, Flow obj) {
        try {
            Flow entity = repository.getById(id);
            if (obj.getData() != null) {
                entity.setData(obj.getData());
            }
            if (obj.getValor() != null) {
                entity.setValor(obj.getValor());
            }
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(id);
        }
    }

}
