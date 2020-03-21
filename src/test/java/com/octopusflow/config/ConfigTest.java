package com.octopusflow.config;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

public class ConfigTest {

    @Test
    public void testConfig() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("test", "test value");
        jsonObject.put("http.port", 8080);
        Config config = jsonObject.mapTo(Config.class);
    }
}
