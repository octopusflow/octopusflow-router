package com.octopusflow.task;

import com.octopusflow.config.Config;
import org.apache.kafka.streams.Topology;

public interface StreamTask {

    /**
     * Kafka Stream task builder
     * @param taskConf task config
     */
    Topology build(Config.TaskConf taskConf);
}
