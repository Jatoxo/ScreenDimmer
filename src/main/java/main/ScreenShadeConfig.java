package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ScreenShadeConfig {
    int initialDimLevel;
    boolean enableServer;
    boolean startMinimized;

    String mqttClientId;
    String mqttBroker;
    String mqttUsername;
    String mqttPassword;
    String mqttTopicSet;
    String mqttTopicState;

    public ScreenShadeConfig() {}



    static ScreenShadeConfig loadConfig() throws IOException {
        Properties properties = new Properties();
        Path configPath = Paths.get(Main.CONFIG_FILE);

        // Read the config file
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            properties.load(reader);
        }

        System.out.printf("Loaded config from: %s%n", configPath.toAbsolutePath());

        // Create a ScreenShadeConfig object from the properties
        ScreenShadeConfig config = new ScreenShadeConfig();
        config.initialDimLevel = Integer.parseInt(properties.getProperty("initialDimLevel", "0"));
        config.enableServer = Boolean.parseBoolean(properties.getProperty("enableServer", "true"));
        config.startMinimized = Boolean.parseBoolean(properties.getProperty("startMinimized", "false"));

        config.mqttBroker = properties.getProperty("mqtt.broker");
        config.mqttUsername = properties.getProperty("mqtt.username");
        config.mqttPassword = properties.getProperty("mqtt.password");
        config.mqttClientId = properties.getProperty("mqtt.clientId");
        config.mqttTopicSet = properties.getProperty("mqtt.topicSet");
        config.mqttTopicState = properties.getProperty("mqtt.topicState");

        return config;
    }


    static void ensureConfigFile() throws IOException {
        Path configPath = Paths.get(Main.CONFIG_FILE);

        if (!Files.exists(configPath)) {
            System.out.println("Config file not found.");

            // Copy default config file from resources
            try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("default_config.properties")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Default config file not found in resources.");
                }

                Files.copy(inputStream, configPath);
                System.out.println("Created default config file at: " + configPath.toAbsolutePath());
            }
        }

    }

}
