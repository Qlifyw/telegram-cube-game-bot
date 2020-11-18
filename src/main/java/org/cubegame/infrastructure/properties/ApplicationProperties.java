package org.cubegame.infrastructure.properties;

import org.cubegame.infrastructure.exceptions.DiskIOException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static final String PROPERTY_BOT_NAME = "bot.name";
    private static final String PROPERTY_BOT_TOKEN = "bot.token";

    private static final String PROPERTY_DATABASE_HOST = "db.host";
    private static final String PROPERTY_DATABASE_PORT = "db.port";
    private static final String PROPERTY_DATABASE_USER = "db.user";
    private static final String PROPERTY_DATABASE_PASS = "db.pass";
    private static final String PROPERTY_DATABASE_NAME = "db.name";

    private static final Properties properties = new Properties();

    private static final InputStream properties2 = ApplicationProperties.class.getResourceAsStream("/application.properties");

//    private static final File defaultPropertiesFile = new File("src/main/resources/application.properties");


    public ApplicationProperties() {
    }

    public ApplicationProperties(final String botName) {
        properties.put(PROPERTY_BOT_NAME, botName);
    }

    public static ApplicationProperties load() {
        System.out.println(System.getProperty("user.dir"));
        tryLoad(/*defaultPropertiesFile*/);
        return new ApplicationProperties();
    }

    public static ApplicationProperties load(File propsFile) {
        tryLoad(/*propsFile*/);
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

    private static void tryLoad(/*final File propertiesFile*/) {
        try /*(FileInputStream fileInputStream = new FileInputStream(propertiesFile))*/ {
            properties.load(properties2);
        } catch (IOException e) {
            throw new DiskIOException(String.format("Cannot load property file '%s'", /*propertiesFile*/ properties2 ), e);
        }
    }

}