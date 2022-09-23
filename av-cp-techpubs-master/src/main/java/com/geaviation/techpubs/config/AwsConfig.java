package com.geaviation.techpubs.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.PredefinedClientConfigurations;

public class AwsConfig {
    private static final String proxyHost = "PITC-Zscaler-Americas-Cincinnati3PR.proxy.corporate.ge.com";
    private static final int proxyPort = 80;

    /**
     * Client configuration necessary to locally invoke a remote lambda function
     * @return Client configuration
     */
    public static ClientConfiguration getLocalClientConfiguration() {
        ClientConfiguration configuration = PredefinedClientConfigurations.defaultConfig();
        System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");
        configuration
                .withProxyHost(proxyHost)
                .withProxyPort(proxyPort)
                .withNonProxyHosts("localhost");
        return configuration;
    }
}
