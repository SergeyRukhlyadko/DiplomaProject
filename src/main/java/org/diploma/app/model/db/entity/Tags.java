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
import javax.persistence.OneToOne;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false, unique = true)
    String name;

    @OneToMany(mappedBy = "tag")
    Set<Tag2post> tag2posts;

    @OneToOne(mappedBy = "tag")
    TagStatistic tagStatistic;

    public Tags(int id) {
        this.id = id;
    }

    public Tags(String name) {
        this.name = name;
    }
}
