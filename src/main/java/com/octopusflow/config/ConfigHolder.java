package com.octopusflow.config;

import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigHolder {
    private static Config config;

    public static synchronized void reload(JsonObject jsonObject) {
        if (jsonObject != null) {
            log.info("reload config with JsonObject {}", jsonObject);
            config = jsonObject.mapTo(Config.class);
            log.info("config instance: {}", config);
        }
    }

    public static synchronized Config getConfig() {
        return config;
    }
}
