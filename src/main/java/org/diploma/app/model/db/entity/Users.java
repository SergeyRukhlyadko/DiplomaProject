package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "is_moderator", nullable = false)
    boolean isModerator;

    @Column(name = "reg_time",nullable = false)
    LocalDateTime regTime;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    String code;

    @Column(columnDefinition = "TEXT")
    String photo;

    @OneToMany(mappedBy = "moderatorId")
    List<Posts> postsModerator;

    @OneToMany(mappedBy = "userId")
    List<Posts> postsUser;

    @OneToMany(mappedBy = "userId")
    List<PostVotes> postVotes;

    @OneToMany(mappedBy = "userId")
    List<PostComments> postComments;
}
