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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

/**
 * Способ связи
 */
@Entity
@Table(name = "contact_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContactDetail {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
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
     * Лейбл способа связи
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
    @Builder.Default
    private Boolean isPrimary = false;
}
