package com.octopusflow.predicate;

import com.fasterxml.jackson.databind.JsonNode;
import com.octopusflow.util.Util;
import org.apache.kafka.streams.kstream.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MaxwellKeyPredicate implements Predicate<JsonNode, byte[]> {

    private static final String DATABASE = "database";
    private static final String TABLE = "table";
    private Set<String> whitelist = new HashSet<>();
    private List<Pattern> whitelistRegex = new ArrayList<>();

    public MaxwellKeyPredicate(List<String> lists) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        for (String s : lists) {
            if (Util.isValidRegex(s)) {
                String pattern = s.substring(2, s.length()-1);
                whitelistRegex.add(Pattern.compile(pattern));
            } else {
                whitelist.add(s);
            }
        }
    }

    public boolean test(JsonNode key, byte[] value) {
        if (key == null || !key.hasNonNull(DATABASE) || !key.hasNonNull(TABLE)) {
            return false;
        }
        String database = key.get(DATABASE).asText();
        String table = key.get(TABLE).asText();
        String dbTable = String.format("%s.%s", database, table);
        String dbAllTable = String.format("%s.%s", database, "*");
        // first whitelist, then regex mode
        if (whitelist.contains(dbTable) || whitelist.contains(dbAllTable)) {
            return true;
        }
        for (Pattern p : whitelistRegex) {
            if (p.matcher(dbTable).matches()) {
                return true;
            }
        }
        return false;
    }
}
