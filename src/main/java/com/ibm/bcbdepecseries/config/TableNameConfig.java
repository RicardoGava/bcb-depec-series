package com.ibm.bcbdepecseries.config;

import com.ibm.bcbdepecseries.config.impl.PhysicalNamingImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableNameConfig {

    @Bean
    public PhysicalNamingStrategyStandardImpl physicalNamingStrategyStandard(){
        return new PhysicalNamingImpl();
    }

}
