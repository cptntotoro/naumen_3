package ru.anastasia.NauJava.service.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.tag.ContactTag;
import ru.anastasia.NauJava.entity.tag.Tag;
import ru.anastasia.NauJava.exception.tag.IllegalTagStateException;
import ru.anastasia.NauJava.exception.tag.TagNotFoundException;
import ru.anastasia.NauJava.repository.tag.ContactTagRepository;
import ru.anastasia.NauJava.repository.tag.TagRepository;
import ru.anastasia.NauJava.service.contact.ContactService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    /**
     * Репозиторий тегов
     */
    private final TagRepository tagRepository;

    /**
     * Репозиторий связи контактов и тегов
     */
    private final ContactTagRepository contactTagRepository;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    @Override
    public Tag create(Tag tag) {
        log.info("Создание тега: {}", tag.getName());

        try {
            Tag savedTag = tagRepository.save(tag);
            log.info("Тег успешно создан. ID: {}, имя: {}, цвет: {}",
                    savedTag.getId(), savedTag.getName(), savedTag.getColor());
            return savedTag;
        } catch (DataIntegrityViolationException e) {
            log.warn("Ошибка создания тега: {}. Тег с таким именем уже существует", tag.getName());
            throw new IllegalTagStateException("Не удалось создать тег: " + tag.getName() + ". " +
                    "Тег с таким именем уже существует");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании тега: {}. Причина: {}",
                    tag.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        log.debug("Поиск тега по имени: {}", name);

        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("Тег не найден по имени: {}", name);
                    return new TagNotFoundException("Тег не найден: " + name);
                });

        log.debug("Тег найден: ID: {}, имя: {}", tag.getId(), tag.getName());
        return tag;
    }

    @Override
    public ContactTag addToContact(Long contactId, String tagName) {
        log.info("Добавление тега '{}' к контакту ID: {}", tagName, contactId);

        Contact contact = contactService.findById(contactId);
        log.debug("Контакт найден: ID: {}, имя: {}", contactId, contact.getFullName());

        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> {
                    log.warn("Тег не найден [Название: {}]", tagName);
                    return new TagNotFoundException("Тег не найден с названием: " + tagName);
                });

        log.debug("Используется тег: ID: {}, имя: {}", tag.getId(), tag.getName());

        boolean alreadyExists = contactTagRepository.existsByContactIdAndTagId(contactId, tag.getId());
        if (alreadyExists) {
            log.warn("Попытка добавить уже существующий тег. Контакт ID: {}, тег: '{}'", contactId, tagName);
            throw new IllegalTagStateException("Тег уже привязан к контакту");
        }

        ContactTag contactTag = ContactTag.builder()
                .contact(contact)
                .tag(tag)
                .build();

        ContactTag savedContactTag = contactTagRepository.save(contactTag);

        log.info("Тег '{}' успешно добавлен к контакту. ID связи: {}, контакт: {}, тег ID: {}",
                tagName, savedContactTag.getId(), contact.getFullName(), tag.getId());

        return savedContactTag;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByContactId(Long contactId) {
        log.debug("Поиск тегов для контакта ID: {}", contactId);

        List<Tag> tags = contactTagRepository.findByContactId(contactId)
                .stream()
                .map(ContactTag::getTag)
                .collect(Collectors.toList());

        log.debug("Найдено {} тегов для контакта ID: {}", tags.size(), contactId);

        return tags;
    }

    @Override
    public List<Tag> findAll() {
        log.debug("Получение всех тегов");

        List<Tag> tags = StreamSupport.stream(tagRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        log.debug("Найдено {} тегов в системе", tags.size());

        return tags;
    }

    @Override
    public Tag findById(Long id) {
        log.debug("Поиск тега по ID: {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Тег не найден с ID: {}", id);
                    return new TagNotFoundException("Тег не найден с id: " + id);
                });

        log.debug("Тег найден: ID: {}, имя: {}", tag.getId(), tag.getName());
        return tag;
    }

    @Override
    public List<Tag> findAllById(Collection<Long> ids) {
        log.debug("Поиск тегов по списку ID. Количество ID: {}", ids.size());

        List<Tag> tags = tagRepository.getTagsByIdIsIn(ids);

        log.debug("Найдено {} тегов из запрошенных {}", tags.size(), ids.size());

        return tags;
    }

    @Override
    public Tag update(Tag tag) {
        log.info("Обновление тега ID: {}, новое имя: {}", tag.getId(), tag.getName());

        Long id = tag.getId();
        Tag foundTag = findById(id);

        log.debug("Текущие данные тега: имя: {}, цвет: {}",
                foundTag.getName(), foundTag.getColor());

        foundTag.setName(tag.getName());
        foundTag.setColor(tag.getColor());
        foundTag.setDescription(tag.getDescription());

        try {
            Tag updatedTag = tagRepository.save(foundTag);
            log.info("Тег успешно обновлен. ID: {}, имя: {}, цвет: {}",
                    updatedTag.getId(), updatedTag.getName(), updatedTag.getColor());
            return updatedTag;
        } catch (DataIntegrityViolationException e) {
            log.warn("Ошибка обновления тега ID: {}. Тег с именем '{}' уже существует",
                    id, tag.getName());
            throw new IllegalTagStateException("Тег с таким именем уже существует");
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении тега ID: {}. Причина: {}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление тега ID: {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удаления несуществующего тега ID: {}", id);
                    return new TagNotFoundException("Тег не найден с id: " + id);
                });

        log.debug("Тег для удаления: ID: {}, имя: {}", tag.getId(), tag.getName());

        tagRepository.deleteById(id);

        log.info("Тег успешно удален. ID: {}, имя: {}", id, tag.getName());
    }

    @Override
    public List<ContactTag> findContactTagsByContactId(Long contactId) {
        log.debug("Поиск связей контакт-тег для контакта ID: {}", contactId);

        List<ContactTag> contactTags = contactTagRepository.findByContactId(contactId);

        log.debug("Найдено {} связей контакт-тег для контакта ID: {}",
                contactTags.size(), contactId);

        return contactTags;
    }

    @Override
    public void deleteContactTag(Long contactTagId) {
        log.info("Удаление связи контакт-тег ID: {}", contactTagId);

        ContactTag contactTag = contactTagRepository.findById(contactTagId)
                .orElseThrow(() -> {
                    log.warn("Попытка удаления несуществующей связи контакт-тег ID: {}", contactTagId);
                    return new TagNotFoundException("Связь контакт-тег не найдена с id: " + contactTagId);
                });

        log.debug("Связь для удаления: ID: {}, контакт ID: {}, тег ID: {}",
                contactTagId, contactTag.getContact().getId(), contactTag.getTag().getId());

        contactTagRepository.deleteById(contactTagId);

        log.info("Связь контакт-тег успешно удалена. ID: {}, контакт: {}, тег: {}",
                contactTagId, contactTag.getContact().getFullName(), contactTag.getTag().getName());
    }
}