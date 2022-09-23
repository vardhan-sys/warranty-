package com.geaviation.techpubs.data.util;

import com.geaviation.dss.service.common.util.DataServiceUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class MongoConfiguration {

    @Value("${SPRING.DATA.MONGODB.USERNAME}")
    private String mongoDatabaseUserName;

    @Value("${SPRING.DATA.MONGODB.PASSWORD}")
    private String mongoDatabasePassword;

    @Value("${SPRING.DATA.MONGODB.REPLICASET}")
    private String mongoDatabaseReplicaset;

    @Value("${SPRING.DATA.MONGODB.DB}")
    private String mongoDatabaseDb;

    @Value("${SPRING.DATA.MONGODB.URL}")
    private String mongoDatabaseUrl;

    @Value("${ENCRYPTIONALGO}")
    private String encryptionAlgo;

    @Value("${ENCRYPTIONPWD}")
    private String deccryptionkey;

    @Value("${SPRING.DATA.MONGODB.OPTIONS}")
    private String mongoDatabaseOptions;

    private MongoClient mongo;

    protected String getDatabaseName() {
        return mongoDatabaseDb;
    }

    @Bean
    public MongoClient mongo() throws IOException {
        this.mongo = new MongoClient(createMongoClientURI());
        return this.mongo;
    }

//    @PreDestroy
//    public void close() {
//        if (this.mongo != null) {
//            this.mongo.close();
//        }
//    }

    private MongoClientURI createMongoClientURI() throws IOException {

        final StringBuilder uriVal = new StringBuilder(mongoDatabaseUrl);
        if (mongoDatabaseUserName != null) {
            uriVal.append(mongoDatabaseUserName);
        }

        if (mongoDatabasePassword != null) {
            uriVal.append(':')
                .append(DataServiceUtils.getSecurePassword("SPRING.ORA_WARRANTY.username",
                    mongoDatabasePassword, encryptionAlgo, deccryptionkey));

        }

        uriVal.append('@');

        uriVal.append(mongoDatabaseReplicaset).append('/');

        if (mongoDatabaseDb != null) {
            uriVal.append(mongoDatabaseDb);
        }

        if (mongoDatabaseOptions != null) {
            uriVal.append(mongoDatabaseOptions);
        }

        return new MongoClientURI(uriVal.toString());
    }
}