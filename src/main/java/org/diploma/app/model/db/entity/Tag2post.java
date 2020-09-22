package org.diploma.app.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Tag2post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Posts post;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    Tags tag;

    public Tag2post(Posts post, Tags tag) {
        this.post = post;
        this.tag = tag;
    }
}
