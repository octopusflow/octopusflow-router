package com.octopusflow.task;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTask {

    private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);

    // JSON untyped Serde
    private final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());
    protected final Consumed<JsonNode, JsonNode> jsonConsumed = Consumed.with(jsonSerde, jsonSerde);
    protected final Produced<JsonNode, JsonNode> jsonProduced = Produced.with(jsonSerde, jsonSerde);
    protected final Consumed<String, JsonNode> stringJsonConsumed = Consumed.with(Serdes.String(), jsonSerde);
    protected final Produced<String, JsonNode> stringJsonProduced = Produced.with(Serdes.String(), jsonSerde);
    protected final Consumed<JsonNode, byte[]> jsonBytesConsumed = Consumed.with(jsonSerde, Serdes.ByteArray());
    protected final Consumed<byte[], byte[]> bytesConsumed = Consumed.with(Serdes.ByteArray(), Serdes.ByteArray());
    protected final Produced<JsonNode, byte[]> jsonBytesProduced = Produced.with(jsonSerde, Serdes.ByteArray());
    protected final Produced<byte[], byte[]> bytesProduced = Produced.with(Serdes.ByteArray(), Serdes.ByteArray());
}
