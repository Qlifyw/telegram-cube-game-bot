package org.cubegame.infrastructure.properties;


import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

public class ApplicationProperties {

    private static final String PROPERTY_BOT_NAME = "bot.name";
    private static final String PROPERTY_BOT_TOKEN = "bot.token";

    private static final String PROPERTY_DATABASE_HOST = "db.host";
    private static final String PROPERTY_DATABASE_PORT = "db.port";
    private static final String PROPERTY_DATABASE_USER = "db.user";
    private static final String PROPERTY_DATABASE_PASS = "db.pass";
    private static final String PROPERTY_DATABASE_NAME = "db.name";

    private static final String DEFAULT_FILE_PATH = "/application.properties";

    private static final Properties properties = new Properties();

    private static final InputStream inputStream = ApplicationProperties.class.getResourceAsStream(DEFAULT_FILE_PATH);

    public ApplicationProperties() {
    }

    public ApplicationProperties(final String botName) {
        properties.put(PROPERTY_BOT_NAME, botName);
    }

    public static ApplicationProperties load() {
        tryLoad(inputStream);
        return new ApplicationProperties();
    }

    public static ApplicationProperties load(String path) {
        final InputStream inputStream = ApplicationProperties.class.getResourceAsStream(path);
        tryLoad(inputStream);
        return new ApplicationProperties();
    }

    public String getBotName() {
        return properties.getProperty(PROPERTY_BOT_NAME);
    }

    public String getBotToken() {
        return properties.getProperty(PROPERTY_BOT_TOKEN);
    }

    public String getDatabaseHost() {
        return properties.getProperty(PROPERTY_DATABASE_HOST);
    }

    public String getDatabasePort() {
        return properties.getProperty(PROPERTY_DATABASE_PORT);
    }

    public String getDatabaseUser() {
        return properties.getProperty(PROPERTY_DATABASE_USER);
    }

    public String getDatabasePass() {
        return properties.getProperty(PROPERTY_DATABASE_PASS);
    }

    public String getDatabaseName() {
        return properties.getProperty(PROPERTY_DATABASE_NAME);
    }

    private static void tryLoad(final InputStream inputStream) {
        try {
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new InternalError(
                    Internal.Logical.INCONSISTENCY,
                    String.format("Cannot load property file '%s'", /*propertiesFile*/ inputStream),
                    Collections.emptyMap(),
                    exception
            );
        }
    }
}