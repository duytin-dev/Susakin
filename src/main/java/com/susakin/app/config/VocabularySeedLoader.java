package com.susakin.app.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VocabularySeedLoader {

    private static final String SEED_PATH = "seed/vocabularies.json";

    private final ObjectMapper objectMapper;

    public Map<String, List<WordEntry>> load() {
        try (InputStream input = new ClassPathResource(SEED_PATH).getInputStream()) {
            Map<String, List<List<String>>> raw = objectMapper.readValue(
                    input,
                    new TypeReference<Map<String, List<List<String>>>>() {}
            );

            return raw.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .map(pair -> new WordEntry(pair.get(0), pair.get(1)))
                                    .toList()
                    ));
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc file seed từ vựng: " + SEED_PATH, e);
        }
    }

    public List<WordEntry> loadForTopic(String topicName) {
        return load().getOrDefault(topicName, Collections.emptyList());
    }

    public record WordEntry(String word, String meaningVi) {}
}
