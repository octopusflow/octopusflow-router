package com.octopusflow.task.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.octopusflow.predicate.MaxwellKeyPredicate;
import com.octopusflow.task.AbstractTask;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.List;

public class MaxwellKeyTask extends AbstractTask {

    public Topology build(List<String> sourceTopics, List<String> whitelist, String sinkTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        final KStream<JsonNode, byte[]> sourceStream = builder.stream(sourceTopics, jsonBytesConsumed);
        sourceStream.filter(new MaxwellKeyPredicate(whitelist)).to(sinkTopic, jsonBytesProduced);
        return builder.build();
    }
}
