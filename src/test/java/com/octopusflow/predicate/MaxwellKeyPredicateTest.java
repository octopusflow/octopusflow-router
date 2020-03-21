package com.octopusflow.predicate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.streams.kstream.Predicate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MaxwellKeyPredicateTest {

    @Test
    public void testPredicate() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node1 = mapper.createObjectNode();
        node1.put("database", "user");
        node1.put("table", "account");
        ObjectNode node2 = mapper.createObjectNode();
        node2.put("database", "user_01");
        node2.put("table", "account");
        ObjectNode node3 = mapper.createObjectNode();
        node3.put("database", "user01");
        node3.put("table", "account");

        List<String> whitelist1 = new ArrayList<>();
        whitelist1.add("user.account");
        Predicate<JsonNode, byte[]> predicate1 = new MaxwellKeyPredicate(whitelist1);
        assert predicate1.test(node1, null);
        assert !predicate1.test(node2, null);

        List<String> whitelist2 = new ArrayList<>();
        whitelist2.add("user.*");
        Predicate<JsonNode, byte[]> predicate2 = new MaxwellKeyPredicate(whitelist2);
        assert predicate2.test(node1, null);
        assert !predicate2.test(node2, null);

        List<String> whitelist3 = new ArrayList<>();
        whitelist3.add("r'user(_\\w+)?\\.\\w+'");
        Predicate<JsonNode, byte[]> predicate3 = new MaxwellKeyPredicate(whitelist3);
        assert predicate3.test(node1, null);
        assert predicate3.test(node2, null);
        assert !predicate3.test(node3, null);
    }
}
