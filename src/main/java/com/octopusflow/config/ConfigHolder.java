package com.octopusflow.config;

import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigHolder {
    private static String raw;
    private static Config config = new Config();

    public static synchronized boolean reload(JsonObject jsonObject) {
        if (jsonObject != null) {
            String newRaw = jsonObject.toString();
            log.info("reload config with raw before {}, after {}", raw, newRaw);
            raw = newRaw;
            Config newConfig = jsonObject.mapTo(Config.class);
            String oldJson = config.toJson();
            String newJson = newConfig.toJson();
            if (oldJson.equals(newJson)) {
                log.info("config is not changed");
                return false;
            }
            log.info("reload config json with raw before {}, after {}", oldJson, newJson);
            config = newConfig;
        } else {
            log.warn("json object is null");
            return false;
        }
        return true;
    }

    public static synchronized Config getConfig() {
        return config;
    }
}
