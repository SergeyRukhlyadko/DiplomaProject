package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Integer> {

    Optional<Tags> findByName(String name);
}
