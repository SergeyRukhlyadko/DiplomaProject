package org.diploma.app.model.db.repository;

import org.diploma.app.model.db.entity.PostVotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVotes, Integer> {}
