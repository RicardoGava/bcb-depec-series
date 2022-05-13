package com.ibm.bcbdepecseries.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeriesSum implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer ano;
    @JsonProperty("valores-somados")
    private Integer valoresSomados;
    private BigDecimal total;

}
