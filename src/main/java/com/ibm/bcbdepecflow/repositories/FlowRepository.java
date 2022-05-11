package com.ibm.bcbdepecflow.repositories;

import com.ibm.bcbdepecflow.domain.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FlowRepository extends JpaRepository<Flow, Long> {

    //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    //https://springhow.com/spring-mvc-pagination-sorting/

    Page<Flow> findAllByDataBetween(LocalDate dataInicial, LocalDate dataFinal, Pageable pageable);

    Page<Flow> findAllByDataGreaterThanEqual(LocalDate dataInicial, Pageable pageable);

    Page<Flow> findAllByDataLessThanEqual(LocalDate dataFinal, Pageable pageable);

    // Para reutilização do código precisei tirar os Queries, pois infelizmente
    // não era possível alterar o nome da tabela selecionada por variável

    /*@Query(value = "SELECT * FROM (SELECT * FROM dados " +
            "ORDER BY data DESC LIMIT :valores) ORDER BY data ASC", nativeQuery = true)
    List<Flow> findLastValues(Integer valores);*/

    /*@Query(value = "SELECT YEAR(data), COUNT(*), SUM(valor) FROM dados " +
            "GROUP BY YEAR(data)", nativeQuery = true)
    List<List<Object>> getAllYearsTotal();*/

    /*@Query(value = "SELECT YEAR(data), COUNT(*), SUM(valor) FROM dados " +
            "WHERE YEAR(data) = :ano GROUP BY YEAR(data)", nativeQuery = true)
    List<List<Object>> getYearTotal(String ano);*/

}
