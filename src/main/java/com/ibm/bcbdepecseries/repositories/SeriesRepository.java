package com.ibm.bcbdepecseries.repositories;

import com.ibm.bcbdepecseries.domain.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    // https://springhow.com/spring-mvc-pagination-sorting/

    Page<Series> findAllByDataBetween(LocalDate dataInicial, LocalDate dataFinal, Pageable pageable);

    Page<Series> findAllByDataGreaterThanEqual(LocalDate dataInicial, Pageable pageable);

    Page<Series> findAllByDataLessThanEqual(LocalDate dataFinal, Pageable pageable);

}
