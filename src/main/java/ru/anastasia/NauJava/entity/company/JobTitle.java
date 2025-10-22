package ru.anastasia.NauJava.entity.company;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

/**
 * Должность
 */
@Entity
@Table(
        name = "job_titles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "title", name = "uk_job_titles_title")
        }
)
public class JobTitle {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название
     */
    @Column(nullable = false, unique = true)
    private String title;

    /**
     * Контакты
     */
    @OneToMany(mappedBy = "jobTitle", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactCompany> contacts = new HashSet<>();

    public JobTitle() {
    }

    public JobTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<ContactCompany> getContacts() {
        return contacts;
    }

    public void setContacts(Set<ContactCompany> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "JobTitle{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contacts=" + contacts +
                '}';
    }
}
