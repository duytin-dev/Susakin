package com.susakin.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Vocabulary> vocabularies = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Lesson> lessons = new ArrayList<>();
}