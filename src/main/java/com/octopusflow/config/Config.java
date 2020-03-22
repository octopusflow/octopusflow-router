package com.octopusflow.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Config {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class TaskConf {
        private KafkaConf kafka;
        private List<String> sourceTopics;
        private Map<String, RouterConf> sinkRouters;
        // class name of StreamTask
        private String taskType;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class KafkaConf {
        // consumer group, AKA application.id in kafka stream
        private String group;
        // bootstrap servers
        private String servers;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class RouterConf {
        private List<String> whitelist;
    }

    // task config
    private Map<String, TaskConf> task;
    // global kafka config
    private KafkaConf kafka;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
        }
        return "{}";
    }

    public String getAppId(String taskName) {
        if (this.task != null && this.task.get(taskName) != null) {
            TaskConf taskConf = this.task.get(taskName);
            if (taskConf != null && taskConf.kafka != null && taskConf.kafka.group != null) {
                return taskConf.kafka.group;
            }
            return taskName;
        }
        return "";
    }

    public String getBootstrapServers(String taskName) {
        if (this.task == null || this.task.get(taskName) == null) {
            return "";
        }

        TaskConf taskConf = this.task.get(taskName);
        if (taskConf != null && taskConf.kafka != null && taskConf.kafka.servers != null) {
            return taskConf.kafka.servers;
        } else {
            // fallback to global kafka servers
            if (this.kafka != null && this.kafka.servers != null) {
                return this.kafka.servers;
            }
        }
        return "";
    }
}
