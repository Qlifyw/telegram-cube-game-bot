package org.cubegame.infrastructure.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    private final static String PROPERTY_BOT_NAME = "bot_name";
    private final static String PROPERTY_BOT_TOKEN = "bot_token";
    private final static Properties properties = new Properties();
    private final static File propertiesFile = new File("application.properties");


    private ApplicationProperties() {
    }

    public static ApplicationProperties load() {
        tryLoad();
        return new ApplicationProperties();
    }

    private static void tryLoad() {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            // TODO replace with custom exception
            e.printStackTrace();
        }
    }

    public String getBotName() {
        return properties.getProperty(PROPERTY_BOT_NAME);
    }

    public String getBotToken() {
        return properties.getProperty(PROPERTY_BOT_TOKEN);
    }

}