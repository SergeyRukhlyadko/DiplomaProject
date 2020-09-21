package org.diploma.app.repository;

import org.diploma.app.model.db.entity.PostsCountByTagName;
import org.diploma.app.model.db.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Integer> {

    Optional<Tags> findByName(String name);

    @Query(nativeQuery = true, value =
        "select t.name name, count(t2p.tag_id) postsCount " +
        "from tags t join tag2post t2p on t.id = t2p.tag_id join posts p on p.id = t2p.post_id " +
        "where p.is_active = ?1 and p.moderation_status = ?2 " +
        "group by t.name")
    List<PostsCountByTagName> findAllNameAndPostsCountByIsActiveAndModerationStatusGroupByName(boolean isActive, String moderationStatus);
}
