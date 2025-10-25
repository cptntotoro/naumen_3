package ru.anastasia.NauJava.service.tag;

import lombok.RequiredArgsConstructor;
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
        try {
            return tagRepository.save(tag);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalTagStateException("Не удалось создать тег: " + tag.getName() + ". " +
                    "Тег с таким именем уже существует");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new TagNotFoundException("Тег не найден: " + name));
    }

    @Override
    public ContactTag addToContact(Long contactId, String tagName) {
        Contact contact = contactService.findById(contactId);

        Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .name(tagName)
                            .color("#808080")
                            .build();
                    return create(newTag);
                });

        boolean alreadyExists = contactTagRepository.existsByContactIdAndTagId(contactId, tag.getId());
        if (alreadyExists) {
            throw new IllegalTagStateException("Тег уже привязан к контакту");
        }

        ContactTag contactTag = ContactTag.builder()
                .contact(contact)
                .tag(tag)
                .build();
        return contactTagRepository.save(contactTag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByContactId(Long contactId) {
        return contactTagRepository.findByContactId(contactId)
                .stream()
                .map(ContactTag::getTag)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tag> findAll() {
        return StreamSupport.stream(tagRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException("Тег не найден с id: " + id));
    }

    @Override
    public List<Tag> findAllById(Collection<Long> ids) {
        return tagRepository.getTagsByIdIsIn(ids);
    }

    @Override
    public Tag update(Tag tag) {
        Long id = tag.getId();

        Tag foundTag = findById(id);

        foundTag.setName(tag.getName());
        foundTag.setColor(tag.getColor());
        foundTag.setDescription(tag.getDescription());

        try {
            return tagRepository.save(foundTag);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalTagStateException("Тег с таким именем уже существует");
        }
    }

    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<ContactTag> findContactTagsByContactId(Long contactId) {
        return List.of();
    }

    @Override
    public void deleteContactTag(Long contactTagId) {
        contactTagRepository.deleteById(contactTagId);
    }
}