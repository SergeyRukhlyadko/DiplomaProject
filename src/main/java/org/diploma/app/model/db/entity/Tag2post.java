package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Entity
public class Tag2post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Posts postId;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    Tags tagId;
}
