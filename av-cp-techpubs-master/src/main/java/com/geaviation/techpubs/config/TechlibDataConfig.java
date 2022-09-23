package com.geaviation.techpubs.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.geaviation.techpubs.data.api.techlib",
        entityManagerFactoryRef = "techlibEntityManagerFactory",
        transactionManagerRef = "techlibTransactionManager"
)
public class TechlibDataConfig {

    @Value("${techlib.datasource.url}")
    private String url;

    @Value("${techlib.datasource.username}")
    private String username;

    @Value("${techlib.datasource.password}")
    private String password;

    @Value("${techlib.datasource.driver-class-name}")
    private String driverClassName;

    @Primary
    @Bean(name = "techlibDataSource")
    public DataSource techlibPostgresDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Primary
    @Bean(name = "techlibEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean techlibEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            final @Qualifier("techlibDataSource") DataSource techlibPostgresDataSource) {
        Properties hibernateProperty = new Properties();

        LocalContainerEntityManagerFactoryBean techlibEntityManagerFactoryBean =
                builder
                        .dataSource(techlibPostgresDataSource)
                        .packages("com.geaviation.techpubs.models.techlib")
                        .persistenceUnit("techlib")
                        .build();

        techlibEntityManagerFactoryBean.setJpaProperties(hibernateProperty);
        return techlibEntityManagerFactoryBean;
    }

    @Primary
    @Bean(name = "techlibTransactionManager")
    public PlatformTransactionManager techlibTransactionManager(
            @Qualifier("techlibEntityManagerFactory")
                    EntityManagerFactory techlibEntityManagerFactory) {
        return new JpaTransactionManager(techlibEntityManagerFactory);
    }

    @Bean (name="techLibJDBCTemplate")
    public NamedParameterJdbcTemplate techLibJDBCTemplate(@Qualifier("techlibDataSource") DataSource techlibDataSource) {
        return new NamedParameterJdbcTemplate(techlibDataSource);
    }

}
