package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentsRepository extends JpaRepository<PostComments, Integer> {}
