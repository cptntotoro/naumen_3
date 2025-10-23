package ru.anastasia.NauJava.repository.contact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.contact.ContactDetail;
import ru.anastasia.NauJava.entity.enums.DetailLabel;
import ru.anastasia.NauJava.entity.enums.DetailType;

import java.util.List;

/**
 * Репозиторий способов связи
 */
@Repository
public interface ContactDetailRepository extends CrudRepository<ContactDetail, Long> {
    /**
     * Получить способы связи по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список способов связи
     */
    List<ContactDetail> findByContactId(Long contactId);

    /**
     * Получить способы связи по типу способа связи, являющиеся основными,
     * ИЛИ способы связи с указанным лейблом (независимо от типа и основности)
     *
     * @param detailType Тип способа связи
     * @param label      Тип лейбла способа связи
     * @return Список способов связи
     */
    List<ContactDetail> findByDetailTypeAndIsPrimaryTrueOrLabel(DetailType detailType, DetailLabel label);

    /**
     * Получить способы связи по типу способа связи и значению
     *
     * @param detailType Тип способа связи
     * @param value      Зачение
     * @return Список способов связи
     */
    List<ContactDetail> findByDetailTypeAndValueContainingIgnoreCase(DetailType detailType, String value);

    /**
     * Получить основные способы связи по идентификатору контакта
     *
     * @param contactId Идентификатор контакта
     * @return Список способов связи
     */
    @Query("SELECT cd FROM ContactDetail cd WHERE cd.contact.id = :contactId AND cd.isPrimary = true")
    List<ContactDetail> findPrimaryContactDetailsByContactId(@Param("contactId") Long contactId);

    /**
     * Получить способы связи по значению
     *
     * @param value Часть значения
     * @return Список способов связи
     */
    List<ContactDetail> findByValueContainingIgnoreCase(String value);
}
