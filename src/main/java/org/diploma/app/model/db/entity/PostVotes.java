package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_votes")
public class PostVotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users userId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Posts postId;

    @Column(nullable = false)
    LocalDateTime time;

    @Column(nullable = false)
    byte value;
}
