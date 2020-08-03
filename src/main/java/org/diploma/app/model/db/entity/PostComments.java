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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_comments")
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name="parent_id")
    PostComments parentId;

    @OneToMany(mappedBy="parentId")
    List<PostComments> replies;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Posts postId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users userId;

    @Column(nullable = false)
    LocalDateTime time;

    @Column(columnDefinition = "TEXT", nullable = false)
    String text;
}
