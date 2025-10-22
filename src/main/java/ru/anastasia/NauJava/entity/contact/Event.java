package ru.anastasia.NauJava.entity.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.anastasia.NauJava.entity.enums.EventType;

import java.time.LocalDate;

/**
 * Событие
 */
@Entity
@Table(name = "events")
public class Event {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Контакт
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    /**
     * Тип события
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    /**
     * Кастомный тип события
     */
    @Column(name = "custom_event_name")
    private String customEventName;

    /**
     * Дата события
     */
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    /**
     * Заметка
     */
    private String notes;

    /**
     * Флаг, повторяется ли событие ежегодно
     */
    @Column(name = "yearly_recurrence")
    private Boolean yearlyRecurrence = false;

    public Event() {
    }

    public Event(EventType eventType, LocalDate eventDate) {
        this.eventType = eventType;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getCustomEventName() {
        return customEventName;
    }

    public void setCustomEventName(String customEventName) {
        this.customEventName = customEventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getYearlyRecurrence() {
        return yearlyRecurrence;
    }

    public void setYearlyRecurrence(Boolean yearlyRecurrence) {
        this.yearlyRecurrence = yearlyRecurrence;
    }
}