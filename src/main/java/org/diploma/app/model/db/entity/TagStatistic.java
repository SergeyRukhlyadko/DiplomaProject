package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tag_statistic")
public class TagStatistic {

    @Id
    @Column(name = "tag_id")
    int tagId;

    @OneToOne
    @PrimaryKeyJoinColumn
    Tags tag;

    @Column(name = "active_and_moderator_accepted_posts_count")
    int activeAndModeratorAcceptedPostsCount; //by tag

    float weight;

    @Column(name = "normalized_weight")
    float normalizedWeight;

    public TagStatistic(int tagId, int activeAndModeratorAcceptedPostsCount, float weight) {
        this.tagId = tagId;
        this.activeAndModeratorAcceptedPostsCount = activeAndModeratorAcceptedPostsCount;
        this.weight = weight;
    }
}
