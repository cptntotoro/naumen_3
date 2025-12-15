// Инициализация для страницы details
document.addEventListener('DOMContentLoaded', function () {
    // Настройка авто-высоты для textarea
    document.querySelectorAll('.note-edit-textarea').forEach(textarea => {
        textarea.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    });

    // Настройка textarea создания заметки
    const noteTextarea = document.getElementById('noteContent');
    if (noteTextarea) {
        noteTextarea.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }

    // Подтверждение удаления
    document.querySelectorAll('form[data-confirm]').forEach(form => {
        form.addEventListener('submit', function (e) {
            if (!confirm(this.getAttribute('data-confirm'))) {
                e.preventDefault();
            }
        });
    });
});