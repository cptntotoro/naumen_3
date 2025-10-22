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
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

/**
 * Способ связи
 */
@Entity
@Table(name = "contact_details")
public class ContactDetail {
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
     * Тип способа связи
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "detail_type", nullable = false)
    private DetailType detailType;

    /**
     * Тип лейбла способа связи
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "label", nullable = false)
    private DetailLabel label;

    /**
     * Значение
     */
    @Column(nullable = false)
    private String value;

    /**
     * Флаг, является ли основным типом связи
     */
    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    public ContactDetail() {
    }

    public ContactDetail(DetailType detailType, DetailLabel label, String value) {
        this.detailType = detailType;
        this.label = label;
        this.value = value;
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

    public DetailType getDetailType() {
        return detailType;
    }

    public void setDetailType(DetailType detailType) {
        this.detailType = detailType;
    }

    public DetailLabel getLabel() {
        return label;
    }

    public void setLabel(DetailLabel label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        isPrimary = primary;
    }
}
