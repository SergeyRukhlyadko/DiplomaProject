package org.diploma.app.service.db;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Tag2post;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.repository.Tag2postRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class Tag2postDBService {

    @Autowired
    Tag2postRepository tag2postRepository;

    public Tag2post save(Posts post, Tags tag) {
        Tag2post tag2post = new Tag2post();
        tag2post.setPost(post);
        tag2post.setTag(tag);
        return tag2postRepository.save(tag2post);
    }

    public void deleteAll(Iterable<Tag2post> iterable) {
        tag2postRepository.deleteInBatch(iterable);
    }
}
