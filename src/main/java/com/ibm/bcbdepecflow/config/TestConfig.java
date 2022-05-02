package com.ibm.bcbdepecflow.config;

import com.ibm.bcbdepecflow.entities.Flow;
import com.ibm.bcbdepecflow.repositories.FlowRepository;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Profile("test")
@EnableTransactionManagement
public class TestConfig implements CommandLineRunner {

    @Value("${api-bcb-serie}")
    String serie;

    @Autowired
    private FlowRepository flowRepository;

    @Override
    public void run(String... args) throws Exception {

        // Deprecation Notice: https://www.baeldung.com/rest-template
        // https://www.amitph.com/spring-webclient-read-json-data/
        // https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html
        // https://www.baeldung.com/spring-webflux-timeout

        WebClient webClient = WebClient.create();

        Flux<Flow> fluxCashFlow = webClient
                .method(HttpMethod.GET)
                .uri("https://api.bcb.gov.br/dados/serie/bcdata.sgs." + serie + "/dados?formato=json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Flow.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"));

        List<Flow> allFlow = fluxCashFlow
                .collect(Collectors.toList())
                .share().block();

        if (allFlow != null) {
            flowRepository.saveAll(allFlow);
        } else {
            throw new RuntimeException("Não foi possível obter os dados da URI especificada.");
        }
    }
}