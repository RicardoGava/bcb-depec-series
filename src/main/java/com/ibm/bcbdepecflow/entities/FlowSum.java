package com.ibm.bcbdepecflow.entities;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FlowSum implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer ano;
    private Integer valoresSomados;
    private BigDecimal total;

}
