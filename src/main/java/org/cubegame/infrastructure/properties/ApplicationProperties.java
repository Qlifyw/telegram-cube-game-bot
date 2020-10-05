package org.cubegame.infrastructure.properties;

import org.cubegame.domain.exceptions.DiskIOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    private static final String PROPERTY_BOT_NAME = "bot_name";
    private static final String PROPERTY_BOT_TOKEN = "bot_token";
    private static final Properties properties = new Properties();
    private static final File defaultPropertiesFile = new File("application.properties");


    public ApplicationProperties() {
    }

    public ApplicationProperties(final String botName) {
        properties.put(PROPERTY_BOT_NAME, botName);
    }

    public static ApplicationProperties load() {
        tryLoad(defaultPropertiesFile);
        return new ApplicationProperties();
    }

    public static ApplicationProperties load(File propsFile) {
        tryLoad(propsFile);
        return new ApplicationProperties();
    }

    public String getBotName() {
        return properties.getProperty(PROPERTY_BOT_NAME);
    }

    public String getBotToken() {
        return properties.getProperty(PROPERTY_BOT_TOKEN);
    }

    private static void tryLoad(final File propertiesFile) {
        try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new DiskIOException(String.format("Cannot load property file '%s'", propertiesFile), e);
        }
    }

}