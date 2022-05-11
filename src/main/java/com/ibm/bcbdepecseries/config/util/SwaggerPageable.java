package com.ibm.bcbdepecseries.config.util;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class SwaggerPageable {

    @ApiParam(value = "Número de registros por página. Exemplo: 10")
    @Nullable
    private Integer size;

    @ApiParam(value = "Número da página que deseja consultar. Exemplo: 0")
    @Nullable
    private Integer page;

    @ApiParam(value = "Critério de ordem. Exemplo: nomeDaColuna,DESC")
    @Nullable
    private String sort;

}