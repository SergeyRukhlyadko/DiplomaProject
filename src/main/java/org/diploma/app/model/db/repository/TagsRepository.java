package org.diploma.app.model.db.repository;

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
        "select t.name name, count(t2p.tag_id) postsCount from tags t left join tag2post t2p on t.id = t2p.tag_id group by t.name")
    List<PostsCountByTagName> findAllTagNamesAndPostsCountGroupByTagName();

    @Query(nativeQuery = true, value =
        "select t.name name, count(t2p.tag_id) postsCount from tags t left join tag2post t2p on t.id = t2p.tag_id where t.name = ?1")
    Optional<PostsCountByTagName> findTagNameAndPostsCountByTagName(String name);
}
