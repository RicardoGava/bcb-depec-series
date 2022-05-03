package com.ibm.bcbdepecflow.repositories;

import com.ibm.bcbdepecflow.domain.Flow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FlowRepository extends JpaRepository<Flow, Long> {

    //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

    List<Flow> findAllByDataBetween(LocalDate dataInicial, LocalDate dataFinal);

    List<Flow> findAllByDataGreaterThanEqual(LocalDate dataInicial);

    List<Flow> findAllByDataLessThanEqual(LocalDate dataFinal);

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
