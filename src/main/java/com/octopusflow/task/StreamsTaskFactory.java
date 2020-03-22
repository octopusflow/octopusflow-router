package com.octopusflow.task;

import com.octopusflow.config.Config;
import com.octopusflow.config.ConfigHolder;
import com.octopusflow.task.impl.MaxwellKeyTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StreamsTaskFactory {

    private static final Map<String, KafkaStreams> taskHolder = new ConcurrentHashMap<>();

    private static Properties getKafkaProps(String appId, String bootstrapServers) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    public static void start() {
        Config config = ConfigHolder.getConfig();
        if (config == null) {
            log.error("null config");
            return;
        }
        for (Map.Entry<String, Config.TaskConf> entry : config.getTask().entrySet()) {
            String taskName = entry.getKey();
            Config.TaskConf taskConf = entry.getValue();
            String appId = config.getAppId(taskName);
            String servers = config.getBootstrapServers(taskName);
            if (appId.isEmpty() || servers.isEmpty()) {
                log.error("invalid app id {} or servers {}, ignore", appId, servers);
                continue;
            }
            Properties kafkaProps = getKafkaProps(appId, servers);
            if (taskHolder.containsKey(taskName)) {
                log.warn("task {} is initialized, ignore", taskName);
                continue;
            }
            if (taskConf == null) {
                log.error("task config is empty");
                continue;
            }
            KafkaStreams streams = new KafkaStreams(build(taskConf), kafkaProps);
            streams.start();
            taskHolder.put(taskName, streams);
        }
    }

    public static void stop() {
        for (Map.Entry<String, KafkaStreams> entry : taskHolder.entrySet()) {
            String taskName = entry.getKey();
            KafkaStreams streamTask = entry.getValue();
            log.info("stop kafka stream task {}...", taskName);
            streamTask.close();
        }
    }

    private static Topology build(Config.TaskConf taskConf) {
        switch (taskConf.getTaskType()) {
            case "MaxwellKeyTask":
                return new MaxwellKeyTask().build(taskConf);
            case "SensorsValueTask":
                break;
            default:
                break;
        }
        return new MaxwellKeyTask().build(taskConf);
    }
}
