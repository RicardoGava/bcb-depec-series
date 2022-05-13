package com.ibm.bcbdepecseries.services;

import com.ibm.bcbdepecseries.domain.Series;
import com.ibm.bcbdepecseries.domain.SeriesSum;
import com.ibm.bcbdepecseries.repositories.SeriesRepository;
import com.ibm.bcbdepecseries.services.exceptions.IdNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class SeriesService {

    @Autowired
    private SeriesRepository repository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Series> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Series findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Series> findByData(Integer dia, Integer mes, Integer ano) {
        List<Series> list = repository.findAll();
        if (ano != null) {
            list = list.stream().filter(data -> data.getData().getYear() == ano).collect(Collectors.toList());
        }
        if (mes != null) {
            list = list.stream().filter(data -> data.getData().getMonthValue() == mes).collect(Collectors.toList());
        }
        if (dia != null) {
            list = list.stream().filter(data -> data.getData().getDayOfMonth() == dia).collect(Collectors.toList());
        }
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Series> findAllByDataBetween(LocalDate dataInicial, LocalDate datafinal, Pageable pageable) {
        return repository.findAllByDataBetween(dataInicial, datafinal, pageable);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Series> findAllByDataGreaterThanEqual(LocalDate dataInicial, Pageable pageable) {
        return repository.findAllByDataGreaterThanEqual(dataInicial, pageable);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Series> findAllByDataLessThanEqual(LocalDate datafinal, Pageable pageable) {
        return repository.findAllByDataLessThanEqual(datafinal, pageable);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Series> findLastValues(Integer valores) {
        Page<Series> page = repository.findAll(
                PageRequest.of(0, valores, Sort.by(
                        Sort.Direction.DESC, "data")));
        List<Series> list = page.getContent();
        List<Series> reversedList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            reversedList.add(list.get(i));
        }
        return reversedList;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SeriesSum> getYearTotal(String ano) {
        DateTimeFormatter yof = DateTimeFormatter.ofPattern("yyyy");
        List<Series> list = repository.findAll();
        Map<String, List<Series>> groupedByYear = list.stream()
                .collect(Collectors.groupingBy(series -> series.getData().format(yof)));
        List<SeriesSum> sumList = new ArrayList<>();
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
                .sorted(Comparator.comparing(SeriesSum::getAno))
                .collect(Collectors.toList());
    }

    public static SeriesSum genSumList(String key, Map<String, List<Series>> map) {
        List<Series> seriesList = map.get(key);
        SeriesSum seriesSum = new SeriesSum(
                Integer.valueOf(key),
                seriesList.size(),
                new BigDecimal(0)
        );
        for (Series series : seriesList) {
            seriesSum.setTotal(seriesSum.getTotal().add(series.getValor()));
        }
        return seriesSum;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Series insert(Series obj) {
        return repository.save(obj);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Series update(Long id, Series obj) {
        try {
            Series entity = repository.getById(id);
            entity.setData(obj.getData());
            entity.setValor(obj.getValor());
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new IdNotFoundException(id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Series updatePartially(Long id, Series obj) {
        try {
            Series entity = repository.getById(id);
            if (obj.getData() != null) {
                entity.setData(obj.getData());
            }
            if (obj.getValor() != null) {
                entity.setValor(obj.getValor());
            }
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new IdNotFoundException(id);
        }
    }

}
