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
                log.info("received config {}", config);
                // init
                ConfigHolder.reload(config);
                vertx.eventBus().publish("config.change", config);
            }
        });

        retriever.listen(change -> {
            JsonObject previous = change.getPreviousConfiguration();
            JsonObject current = change.getNewConfiguration();
            log.info("raw config string changed from previous {} to current {}", previous, current);
            boolean changed = ConfigHolder.reload(current);
            if (changed) {
                vertx.eventBus().publish("config.change", current);
            }
        });
    }
}
