package org.diploma.app.model.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.db.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class TagsDBService {

    @Autowired
    TagsRepository tagsRepository;

    public Optional<Tags> find(String name) {
        return tagsRepository.findByName(name);
    }

    public Tags save(String name) {
        Tags tag = new Tags();
        tag.setName(name);
        return tagsRepository.save(tag);
    }
}
