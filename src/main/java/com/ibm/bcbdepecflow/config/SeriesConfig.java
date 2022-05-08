package com.ibm.bcbdepecflow.config;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpHeader;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import com.ibm.bcbdepecflow.domain.Flow;
import com.ibm.bcbdepecflow.services.FlowService;
import com.ibm.bcbdepecflow.services.SeriesMetadataHashMapService;
import com.ibm.bcbdepecflow.repositories.FlowRepository;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
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
@EnableTransactionManagement
public class SeriesConfig implements CommandLineRunner {

    @Value("${api-bcb-serie}")
    String serie;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private SeriesMetadataHashMapService seriesMetadata;

    @Override
    public void run(String... args) throws Exception {

        // Deprecation Notice: https://www.baeldung.com/rest-template
        // https://www.amitph.com/spring-webclient-read-json-data/
        // https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html
        // https://www.baeldung.com/spring-webflux-timeout

        // Onboarding:

        if (flowRepository.findAll().isEmpty() == true) {
            WebClient webClient = WebClient.create();

            Flux<Flow> fluxFlow = webClient
                    .method(HttpMethod.GET)
                    .uri("https://api.bcb.gov.br/dados/serie/bcdata.sgs." + serie + "/dados?formato=json")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Flow.class)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"));

            List<Flow> flowList = fluxFlow
                    .collect(Collectors.toList())
                    .share().block();

            if (flowList != null) {
                flowRepository.saveAll(flowList);
            } else {
                throw new RuntimeException("Não foi possível obter os dados da URI especificada.");
            }
        }

        // Obtenção dos metadados:

        final com.gargoylesoftware.htmlunit.WebClient pageLoader =
                new com.gargoylesoftware.htmlunit.WebClient(BrowserVersion.FIREFOX);
        pageLoader.addRequestHeader(HttpHeader.ACCEPT_LANGUAGE, "pt-BR");
        pageLoader.getOptions().setJavaScriptEnabled(true);
        pageLoader.getOptions().setThrowExceptionOnScriptError(false);
        pageLoader.getOptions().setThrowExceptionOnFailingStatusCode(false);
        pageLoader.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        pageLoader.setCssErrorHandler(new SilentCssErrorHandler());
        pageLoader.getOptions().setDownloadImages(false);
        pageLoader.getOptions().setTimeout(20000);

        seriesMetadata.putMetadataHashMap("serie", serie);

        try {
            final HtmlPage mainPage = pageLoader.getPage("https://www3.bcb.gov.br/sgspub/consultarmetadados/consultarMetadadosSeries.do?method=consultarMetadadosSeriesInternet&hdOidSerieSelecionada=" + serie);

            String stringPage = mainPage.asNormalizedText();

            seriesMetadata.putMetadataHashMap("nome", stringPage.substring(
                            (stringPage.indexOf("Nome completo ") + 14),
                            stringPage.indexOf(" Nome abreviado")
                    )
            );
            seriesMetadata.putMetadataHashMap("unidadePadrao", stringPage.substring(
                            (stringPage.indexOf("Unidade padrão ") + 15),
                            stringPage.indexOf(" Fonte")
                    )
            );
            seriesMetadata.putMetadataHashMap("fonte", stringPage.substring(
                            (stringPage.indexOf("Fonte ") + 6),
                            stringPage.indexOf(" Data início")
                    )
            );

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }
}