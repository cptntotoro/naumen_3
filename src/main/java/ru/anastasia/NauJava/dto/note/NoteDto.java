package ru.anastasia.NauJava.dto.note;

public class NoteDto {
    private Long id;
    private String content;

    public NoteDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
