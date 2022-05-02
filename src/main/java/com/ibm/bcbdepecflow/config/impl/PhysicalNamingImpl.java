package com.ibm.bcbdepecflow.config.impl;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Value;

public class PhysicalNamingImpl extends PhysicalNamingStrategyStandardImpl {

    @Value("tb_dados_serie_${api-bcb-serie}")
    private String flowTableName;

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        switch (name.getText()) {
            case "Flow":
                return new Identifier(flowTableName, name.isQuoted());
            default:
                return super.toPhysicalTableName(name, context);
        }
    }
}
