package com.geaviation.techpubs.data.factories;

import com.amazonaws.ClientConfiguration;
import com.geaviation.techpubs.exceptions.TechpubsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class ClientConfigurationFactory {

    private static final Logger log = LogManager.getLogger(ClientConfigurationFactory.class);

    @Value("${PROXY}")
    private String proxy;

    @Value("${PROXY.PORT}")
    private String proxyPort;

    @Value("${S3.CLIENT.MAX.CONNECTIONS}")
    private Integer maxConnections;

    /**
     * Factory to create an AWS Client Configuration.  This configuration to create AWS SDK clients
     * when the codebase is ran locally.
     *
     * @return ClientConfiguration - object containing proxy related info for AWS SDK client.
     * @throws TechpubsException - if errors arise during creation of client configuration.
     */
    public ClientConfiguration getClientConfigurations() throws TechpubsException {
        ClientConfiguration config = new ClientConfiguration();
        config.setProxyHost(proxy);
        config.setProxyPort(Integer.parseInt(proxyPort));
        config.withNonProxyHosts("localhost");

        return config;
    }

    /**
     * Factory for ClientConfiguration to set the number of max connections an Amazon service can use in a
     * local environment.
     *
     * @return ClientConfiguration Client configuration for local async clients
     */
    public ClientConfiguration getLocalAsyncClientConfiguration() {
        ClientConfiguration config = new ClientConfiguration();
        config.setProxyHost(proxy);
        config.setProxyPort(Integer.parseInt(proxyPort));
        config.withNonProxyHosts("localhost");
        config.setMaxConnections(maxConnections);

        return config;
    }

    /**
     * Factory for ClientConfiguration to set the number of max connections an Amazon service can use in a
     * remote environment.
     *
     * @return ClientConfiguration Client configuration for remote async clients
     */
    public ClientConfiguration getRemoteAsyncClientConfiguration() {
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxConnections(maxConnections);

        return config;
    }
}
