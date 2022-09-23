package com.geaviation.techpubs.data.util;

import com.geaviation.dss.service.common.util.DataServiceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

    private static final Logger log = LogManager.getLogger(FlywayConfiguration.class);

    @Value("${spring.flyway.url}")
    private String flywayUrl;

    @Value("${spring.flyway.user}")
    private String flywayUsername;

    @Value("${spring.flyway.password}")
    private String flywayPassword;

    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean baselineOnMigrate;

    @Value("${spring.flyway.out-of-order}")
    private boolean outOfOrder;

    @Value("${spring.flyway.baseline-version}")
    private String baselineVersion;

    @Value("${spring.flyway.clean-disabled}")
    private boolean cleanDisabled;

    @Value("${spring.flyway.schemas}")
    private String schemas;

    @Value("${ENCRYPTIONALGO}")
    private String encryptionAlgo;

    @Value("${ENCRYPTIONPWD}")
    private String decryptionkey;

    @ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
    @ConfigurationProperties(prefix = "spring.flyway")
    @Bean(initMethod = "migrate")
    public Flyway flyway() {

        DataSource dataSource = new DataSource();

        try {
            PoolProperties prop = new PoolProperties();
            prop.setUrl(flywayUrl);
            prop.setUsername(flywayUsername);
            prop.setPassword(DataServiceUtils.getSecurePassword("spring.flyway.user", flywayPassword,
                    encryptionAlgo, decryptionkey));
            dataSource = new DataSource(prop);
        } catch (Exception e) {
            log.error(e);
            log.error(e.getMessage());
        }

        return Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemas)
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion(baselineVersion)
                .outOfOrder(outOfOrder)
                .cleanDisabled(cleanDisabled)
                .load();
    }
}
