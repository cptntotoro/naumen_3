package ru.anastasia.NauJava.entity;

/**
 * Контакт
 */
public class Contact {
    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Имя
     */
    private String name;

    /**
     * Номер телефона
     */
    private String phone;

    /**
     * Адрес электронной почты
     */
    private String email;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ID=" + id + ", Name=" + name + ", Phone=" + phone + ", Email=" + email;
    }
}
