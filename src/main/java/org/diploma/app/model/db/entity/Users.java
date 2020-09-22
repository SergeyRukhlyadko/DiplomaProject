package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
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

    @OneToMany(mappedBy = "moderator")
    List<Posts> postsModerator;

    @OneToMany(mappedBy = "user")
    List<Posts> postsUser;

    @OneToMany(mappedBy = "user")
    List<PostVotes> postVotes;

    @OneToMany(mappedBy = "user")
    List<PostComments> postComments;

    /*
        Default regTime is time now
     */
    public Users(boolean isModerator, String name, String email, String password) {
        this.isModerator = isModerator;
        this.regTime = LocalDateTime.now();
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
