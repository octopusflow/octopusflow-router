package com.octopusflow.web;

import com.octopusflow.config.ConfigHolder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.get().handler(BodyHandler.create());
        router.get("/").handler(this::indexHandler);
        router.get("/config").handler(this::getConfigHandler);
        router.post().handler(BodyHandler.create());
        router.post("/config").produces("application/json").handler(this::setConfigHandler);

        vertx.createHttpServer().requestHandler(router).listen(config().getInteger("http.port", 8080));
    }

    private void indexHandler(RoutingContext context) {
        Map<String, Object> baseResponse = getBaseResponse();
        baseResponse.put("data", "index page");
        String response = Json.encodePrettily(baseResponse);
        context.response()
                .putHeader("content-type", "application/json")
                .end(response);
    }

    private void getConfigHandler(RoutingContext context) {
        Map<String, Object> baseResponse = getBaseResponse();
        baseResponse.put("data", ConfigHolder.getConfig());
        String response = Json.encodePrettily(baseResponse);
        context.response()
                .putHeader("content-type", "application/json")
                .end(response);
    }

    private void setConfigHandler(RoutingContext context) {
        log.info("received raw body {}", context.getBodyAsString());
        boolean inValidJson = !isValidJson(context.getBodyAsString());
        Map<String, Object> baseResponse = getBaseResponse();
        String receivedContentType = context.parsedHeaders().contentType().value();
        if (inValidJson || !context.getAcceptableContentType().equals(receivedContentType)) {
            log.warn("received content type {}", receivedContentType);
            baseResponse.put("msg", "invalid content type or json string");
            context.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(400)
                    .end(Json.encodePrettily(baseResponse));
            return;
        }

        JsonObject json = context.getBodyAsJson();
        vertx.eventBus().publish("config.source", json);
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(baseResponse));
    }

    private Map<String, Object> getBaseResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("msg", "ok");
        return response;
    }

    /**
     * Checks if is valid json.
     *
     * @param content
     *          the content
     * @return true, if is valid json
     */
    private boolean isValidJson(String content) {
        try {
            Json.decodeValue(content, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
