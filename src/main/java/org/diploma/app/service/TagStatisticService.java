package org.diploma.app.service;

import org.diploma.app.model.db.entity.projection.TagStatisticNameAndNormalizedWeight;
import org.diploma.app.repository.TagStatisticRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class TagStatisticService {

    private TagStatisticRepository tagStatisticRepository;

    public TagStatisticService(TagStatisticRepository tagStatisticRepository) {
        this.tagStatisticRepository = tagStatisticRepository;
    }

    public Map<String, Float> getAllNormalizedWeight() {
        List<TagStatisticNameAndNormalizedWeight> tags = tagStatisticRepository.findAllNameAndNormalizedWeight();

        if (tags.isEmpty()) {
            return Map.of();
        }

        return tags.stream().collect(toMap(
            TagStatisticNameAndNormalizedWeight::getName,
            TagStatisticNameAndNormalizedWeight::getNormalizedWeight));
    }

    public float getNormalizedWeight(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name can't be null or empty");
        }

        return tagStatisticRepository.findNameAndNormalizedWeightByName(name).orElse(0f);
    }
}
