package com.geaviation.techpubs.data.util;

import com.geaviation.dss.service.common.util.DataServiceUtils;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class TpsConfiguration {

    private static final Logger log = LogManager.getLogger(TpsConfiguration.class);

    @Value("${SPRING.ORACLE.DRIVER_CLASS_NAME}")
    private String oracleDatabaseDriverClassName;

    @Value("${SPRING.ORA_TPS.URL}")
    private String oraDatasourceUrl;

    @Value("${SPRING.ORA_TPS.USERNAME}")
    private String oraDatabaseUserName;

    @Value("${SPRING.ORA_TPS.PASSWORD}")
    private String oraDatabasePassword;

    @Value("${SPRING.DATASOURCE.INITIALSIZE}")
    private String datasourceInitialSize;

    @Value("${SPRING.DATASOURCE.MAXACTIVE}")
    private String datasourceMaxActive;

    @Value("${SPRING.DATASOURCE.MINIDLE}")
    private String datasourceMinIdle;

    @Value("${SPRING.DATASOURCE.MAXIDLE}")
    private String datasourceMaxIdle;

    @Value("${SPRING.DATASOURCE.MAXWAIT}")
    private String datasourceMaxWait;

    @Value("${SPRING.DATASOURCE.TESTONBORROW}")
    private String datasourceTestOnBorrow;

    @Value("${SPRING.DATASOURCE.TESTONRETURN}")
    private String datasourceTestOnReturn;

    @Value("${SPRING.DATASOURCE.TESTWHILEIDLE}")
    private String datasourceTestWhileIdle;

    @Value("${SPRING.DATASOURCE.POOLPREPAREDSTATEMENTS}")
    private String datasourcePoolPreparedStatements;

    @Value("${SPRING.DATASOURCE.ORACLE.VALIDATIONQUERY}")
    private String datasourceValidationQuery;

    @Value("${SPRING.DATASOURCE.TIMEBETWEENEVICTIONRUNSMILLIS}")
    private String datasourceTimeBetweenEvictionRunsMillis;

    @Value("${SPRING.DATASOURCE.MINEVICTABLEIDLETIMEMILLIS}")
    private String datasourceMinEvictableIdleTimeMillis;

    @Value("${ENCRYPTIONALGO}")
    private String encryptionAlgo;

    @Value("${ENCRYPTIONPWD}")
    private String deccryptionkey;

    @Bean(name = "dataSourceTpsORA")
    public DataSource dataSourceTpsORA() throws IOException {
        DataSource dataSource = new DataSource();

        try {

            PoolProperties prop = new PoolProperties();

            prop.setDriverClassName(oracleDatabaseDriverClassName);
            prop.setUrl(oraDatasourceUrl);
            prop.setUsername(oraDatabaseUserName);
            prop.setPassword(
                DataServiceUtils.getSecurePassword("SPRING.ORA_TPS.username", oraDatabasePassword,
                    encryptionAlgo, deccryptionkey));
            prop.setLogValidationErrors(true);
            prop.setValidationQuery(datasourceValidationQuery);
            prop.setInitialSize(Integer.parseInt(datasourceInitialSize));
            prop.setMaxActive(Integer.parseInt(datasourceMaxActive));
            prop.setMinIdle(Integer.parseInt(datasourceMinIdle));
            prop.setMaxIdle(Integer.parseInt(datasourceMaxIdle));
            prop.setMaxWait(Integer.parseInt(datasourceMaxWait));

            prop.setTestOnBorrow(Boolean.parseBoolean(datasourceTestOnBorrow));
            prop.setTestOnReturn(Boolean.parseBoolean(datasourceTestOnReturn));
            prop.setTestWhileIdle(Boolean.parseBoolean(datasourceTestWhileIdle));
            prop.setTimeBetweenEvictionRunsMillis(
                Integer.parseInt(datasourceTimeBetweenEvictionRunsMillis));
            prop.setMinEvictableIdleTimeMillis(
                Integer.parseInt(datasourceMinEvictableIdleTimeMillis));
            prop.setAccessToUnderlyingConnectionAllowed(true);
            dataSource = new DataSource(prop);
        } catch (Exception e) {
            log.error(e);
            log.error(e.getMessage());
        }

        return dataSource;

    }
}
