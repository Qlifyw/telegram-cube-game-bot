package org.cubegame.application.configuration;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;


public class TestDatabaseConfiguration {

    private static MongoDBContainer instance;

    public static synchronized MongoDBContainer getInstance() {
        if (instance == null) {
            instance = new MongoDBContainer((new DockerImageName("mongo:4.0.10")).toString());
            instance.start();
        }
        return instance;
    }

}
