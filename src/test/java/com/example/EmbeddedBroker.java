package com.example;

import com.google.common.io.Files;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.springframework.util.SocketUtils;

import java.io.IOException;

public class EmbeddedBroker {
    public static final int BROKER_PORT = SocketUtils.findAvailableTcpPort();
    private final Broker broker = new Broker();

    public EmbeddedBroker() throws Exception {

        System.out.println("reading broker config");
        final String configFileName = "qpid-config.json";
        final String passwordFileName = "qpidpasswd.properties";
        // prepare options
        final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", String.valueOf(BROKER_PORT));
        brokerOptions.setConfigProperty("qpid.pass_file", findResourcePath(passwordFileName));
        brokerOptions.setConfigProperty("qpid.work_dir", Files.createTempDir().getAbsolutePath());
        brokerOptions.setInitialConfigurationLocation(findResourcePath(configFileName));
        // start broker
        System.out.println("start broker");
        broker.startup(brokerOptions);
        System.out.println("broker started");
    }

    private String findResourcePath(final String file) throws IOException {
        String s = this.getClass().getClassLoader().getResource(file).toString();
        System.out.println("resource file path: " + s);
        return s;
    }
}

