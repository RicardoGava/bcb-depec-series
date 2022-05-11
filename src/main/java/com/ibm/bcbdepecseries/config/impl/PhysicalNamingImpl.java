package com.ibm.bcbdepecseries.config.impl;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Value;

public class PhysicalNamingImpl extends PhysicalNamingStrategyStandardImpl {

    @Value("tb_series_${api-bcb-serie}")
    private String seriesTableName;

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        switch (name.getText()) {
            case "Series":
                return new Identifier(seriesTableName, name.isQuoted());
            default:
                return super.toPhysicalTableName(name, context);
        }
    }
}
