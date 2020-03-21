package com.octopusflow.config;

import com.octopusflow.util.Util;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigVerticle extends AbstractVerticle {

    private static Config config;

    @Override
    public void start() throws Exception {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", Util.getConf()));

        ConfigStoreOptions ebStore = new ConfigStoreOptions()
                .setType("event-bus")
                .setConfig(new JsonObject()
                        .put("address", "config.source")
                );

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore).addStore(ebStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        retriever.getConfig(ar -> {
            if (ar.failed()) {
                // Failed to retrieve the configuration
                log.error(ar.toString());
            } else {
                JsonObject config = ar.result();
                log.info("config {}", config);
                ConfigHolder.reload(config);
            }
        });

        retriever.listen(change -> {
            // Previous configuration
            JsonObject previous = change.getPreviousConfiguration();
            // New configuration
            JsonObject current = change.getNewConfiguration();
            log.info("config changed from previous {} to current {}", previous, current);
            ConfigHolder.reload(current);
            vertx.eventBus().publish("config.change", current);
        });
    }
}
