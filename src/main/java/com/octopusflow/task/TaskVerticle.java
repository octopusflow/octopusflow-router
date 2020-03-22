package com.octopusflow.task;

import io.vertx.core.AbstractVerticle;

public class TaskVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // Register to listen for messages coming IN to the server
        vertx.eventBus().consumer("config.change").handler(message -> {
            // Handle config change, reload task
            StreamsTaskFactory.stop();
            StreamsTaskFactory.start();
        });
    }
}
