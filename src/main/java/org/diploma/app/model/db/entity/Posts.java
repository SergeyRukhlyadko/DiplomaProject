package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false)
    ModerationStatus moderationStatus = ModerationStatus.NEW;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    Users moderatorId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users userId;

    @Column(nullable = false)
    LocalDateTime time;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    String text;

    @Column(name = "view_count", nullable = false)
    int viewCount;

    @OneToMany(mappedBy = "postId")
    List<PostVotes> postVotes;

    @OneToMany(mappedBy = "postId")
    List<PostComments> postComments;

    @OneToMany(mappedBy = "postId")
    Set<Tag2post> tag2posts;
}
