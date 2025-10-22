package ru.anastasia.NauJava.entity.company;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Компания
 */
@Entity
@Table(
        name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name", name = "uk_companies_name")
        }
)
public class Company {
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
    private String name;

    /**
     * Адрес сайта
     */
    private String website;

    /**
     * Контакты
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactCompany> contacts = new HashSet<>();

    /**
     * Дата и время создания
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Company() {
    }

    public Company(Long id, String name, Set<ContactCompany> contacts) {
        this.id = id;
        this.name = name;
        this.contacts = contacts;
    }

    public Company(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<ContactCompany> getContacts() {
        return contacts;
    }

    public void setContacts(Set<ContactCompany> contacts) {
        this.contacts = contacts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", website='" + website + '\'' +
                ", contacts=" + contacts +
                ", createdAt=" + createdAt +
                '}';
    }
}
