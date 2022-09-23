package com.geaviation.techpubs.data.util;

import com.geaviation.dss.service.common.util.DataServiceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.geaviation.techpubs.data.api.cwcadmin"},
        entityManagerFactoryRef = "adminEntityManager",
        transactionManagerRef = "adminTransactionManager"
)
public class AdminConfiguration {

    private static final Logger log = LogManager.getLogger(AdminConfiguration.class);

    @Value("${CWCADMIN.DATASOURCE.DRIVER-CLASS-NAME}")
    private String cwcadminDatabaseDriverClassName;

    @Value("${CWCADMIN.DATASOURCE.USERNAME}")
    private String cwcadminDatabaseUserName;

    @Value("${CWCADMIN.DATASOURCE.PASSWORD}")
    private String cwcadminDatabasePassword;

    @Value("${CWCADMIN.DATASOURCE.URL}")
    private String cwcadminDatabaseUrl;

    @Value("${SPRING.DATASOURCE.MAXACTIVE}")
    private String maxActive;

    @Value("${SPRING.DATASOURCE.MAXIDLE}")
    private String maxIdle;

    @Value("${SPRING.DATASOURCE.MAXWAIT}")
    private String maxWait;

    @Value("${SPRING.DATASOURCE.TESTONBORROW}")
    private String testOnBorrow;

    @Value("${SPRING.DATASOURCE.TESTONRETURN}")
    private String testOnReturn;

    @Value("${SPRING.DATASOURCE.TESTWHILEIDLE}")
    private String testWhileIdle;

    @Value("${SPRING.DATASOURCE.VALIDATIONQUERY}")
    private String validationQuery;

    @Value("${SPRING.DATASOURCE.TIMEBETWEENEVICTIONRUNSMILLIS}")
    private String timeBetweenEvictionRunsMillis;

    @Value("${SPRING.DATASOURCE.MINEVICTABLEIDLETIMEMILLIS}")
    private String minEvictableIdleTimeMillis;

    @Value("${ENCRYPTIONALGO}")
    private String encryptionAlgo;

    @Value("${ENCRYPTIONPWD}")
    private String deccryptionkey;

    @Value("${SPRING.DATASOURCE.INITIALSIZE}")
    private Integer initialSize;


    @Bean(name = "adminEntityManager")
    public LocalContainerEntityManagerFactoryBean adminEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(adminDataSource());
        em.setPackagesToScan(new String[] { "com.geaviation.techpubs.models.cwcadmin" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean(name = "adminDataSource")
    public DataSource adminDataSource() {
        DataSource dataSource = new DataSource();
        try {
            PoolProperties prop = new PoolProperties();
            prop.setDriverClassName(cwcadminDatabaseDriverClassName);
            prop.setUrl(cwcadminDatabaseUrl);
            prop.setUsername(cwcadminDatabaseUserName);
            prop.setMaxActive(Integer.parseInt(maxActive));
            prop.setMaxIdle(Integer.parseInt(maxIdle));
            prop.setMaxWait(Integer.parseInt(maxWait));
            prop.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
            prop.setTestOnReturn(Boolean.parseBoolean(testOnReturn));
            prop.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));
            prop.setValidationQuery(validationQuery);
            prop.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));
            prop.setMinEvictableIdleTimeMillis(Integer.parseInt(minEvictableIdleTimeMillis));
            prop.setPassword(DataServiceUtils.getSecurePassword("CWCADMIN.DATASOURCE.USERNAME", cwcadminDatabasePassword,
                    encryptionAlgo, deccryptionkey));
            prop.setInitialSize(initialSize);
            dataSource = new DataSource(prop);
        } catch (Exception e) {
            log.error(e);
            log.error(e.getMessage());
        }
        return dataSource;
    }

    @Bean(name = "adminTransactionManager")
    public PlatformTransactionManager adminTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(adminEntityManager().getObject());
        return transactionManager;
    }
}
