package ru.anastasia.NauJava.repository.contact.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Кастомный репозиторий контактов
 */
@Repository
public class ContactRepositoryCustomImpl implements ContactRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Contact> findContactsByComplexCriteria(String firstName, String lastName, String company, String jobTitle) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contact> query = cb.createQuery(Contact.class);
        Root<Contact> contact = query.from(Contact.class);

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(cb.lower(contact.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }

        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(cb.lower(contact.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }

        if (company != null && !company.isEmpty()) {
            predicates.add(cb.like(cb.lower(contact.get("company")), "%" + company.toLowerCase() + "%"));
        }

        if (jobTitle != null && !jobTitle.isEmpty()) {
            predicates.add(cb.like(cb.lower(contact.get("jobTitle")), "%" + jobTitle.toLowerCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(contact.get("firstName")), cb.asc(contact.get("lastName")));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Contact> findByFirstNameAndLastNameOrDisplayNameCriteria(String firstName, String lastName, String displayName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contact> query = cb.createQuery(Contact.class);
        Root<Contact> contact = query.from(Contact.class);

        Predicate firstNamePredicate = cb.equal(contact.get("firstName"), firstName);
        Predicate lastNamePredicate = cb.equal(contact.get("lastName"), lastName);
        Predicate andPredicate = cb.and(firstNamePredicate, lastNamePredicate);

        Predicate displayNamePredicate = cb.like(cb.lower(contact.get("displayName")),
                "%" + displayName.toLowerCase() + "%");

        Predicate finalPredicate = cb.or(andPredicate, displayNamePredicate);

        query.where(finalPredicate);
        return entityManager.createQuery(query).getResultList();
    }
}
