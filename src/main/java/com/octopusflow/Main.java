package com.octopusflow;

import com.octopusflow.config.ConfigVerticle;
import com.octopusflow.web.HttpServerVerticle;
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(ConfigVerticle.class.getName());
        vertx.deployVerticle(HttpServerVerticle.class.getName());
    }
}
