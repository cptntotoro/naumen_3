package ru.anastasia.NauJava.entity.tag;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Тег
 */
@Entity
@Table(name = "tags",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = "uk_tags_name")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tag {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /**
     * Название
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Цвет
     */
    private String color;

    /**
     * Описание
     */
    private String description;

    /**
     * Теги
     */
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ContactTag> contactTags = new HashSet<>();
}
