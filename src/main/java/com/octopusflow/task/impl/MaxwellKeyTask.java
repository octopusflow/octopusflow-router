package com.octopusflow.task.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.octopusflow.config.Config.TaskConf;
import com.octopusflow.config.Config.RouterConf;
import com.octopusflow.predicate.MaxwellKeyPredicate;
import com.octopusflow.task.AbstractTask;
import com.octopusflow.task.StreamTask;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.List;
import java.util.Map;

public class MaxwellKeyTask extends AbstractTask implements StreamTask {

    @Override
    public Topology build(TaskConf taskConf) {
        List<String> sourceTopics = taskConf.getSourceTopics();
        Map<String, RouterConf> sinkRouters = taskConf.getSinkRouters();
        StreamsBuilder builder = new StreamsBuilder();
        for (Map.Entry<String, RouterConf> entry : sinkRouters.entrySet()) {
            List<String> whitelist = entry.getValue().getWhitelist();
            String sinkTopic = entry.getKey();
            KStream<JsonNode, byte[]> sourceStream = builder.stream(sourceTopics, jsonBytesConsumed);
            sourceStream.filter(new MaxwellKeyPredicate(whitelist)).to(sinkTopic, jsonBytesProduced);
        }
        return builder.build();
    }
}
