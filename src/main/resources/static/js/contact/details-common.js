// Общие функции для страницы details.html
// Вспомогательные функции для работы с формами
function toggleForm(formId, show) {
    const form = document.getElementById(formId);
    if (!form) return;

    if (show === undefined) {
        show = form.style.display !== 'block';
    }

    if (show) {
        form.style.display = 'block';
        form.style.opacity = '0';
        form.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            form.style.transition = 'all 0.3s ease';
            form.style.opacity = '1';
            form.style.transform = 'translateY(0)';

            // Фокусируемся на первом поле
            const firstInput = form.querySelector('select, input, textarea');
            if (firstInput) setTimeout(() => firstInput.focus(), 10);
        }, 10);
    } else {
        form.style.opacity = '0';
        form.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            form.style.display = 'none';
            // Сбрасываем форму
            const innerForm = form.querySelector('form');
            if (innerForm) innerForm.reset();
        }, 300);
    }
}

// Редактирование элементов
function toggleEdit(elementId, show) {
    const viewElement = document.getElementById(`view-${elementId}`);
    const editElement = document.getElementById(`edit-${elementId}`);

    if (viewElement && editElement) {
        if (show === undefined) {
            show = viewElement.style.display !== 'none';
        }

        if (show) {
            viewElement.style.display = 'none';
            editElement.style.display = 'block';

            // Фокусируемся на первом поле формы редактирования
            const firstInput = editElement.querySelector('select, input, textarea');
            if (firstInput) setTimeout(() => firstInput.focus(), 10);
        } else {
            viewElement.style.display = '';
            editElement.style.display = 'none';

            // Сбрасываем форму редактирования
            const editForm = editElement.querySelector('form');
            if (editForm) editForm.reset();
        }
    }
}

// Автоматическая высота textarea
function setupAutoResizeTextareas() {
    document.querySelectorAll('textarea.auto-resize').forEach(textarea => {
        textarea.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });

        // Инициализация высоты
        textarea.style.height = 'auto';
        textarea.style.height = (textarea.scrollHeight) + 'px';
    });
}

// Обработка ESC
function setupEscapeHandler() {
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            // Список форм, которые можно закрыть
            const closeableForms = [
                'noteFormContainer',
                'eventFormContainer',
                'companyFormContainer',
                'socialFormContainer'
            ];

            closeableForms.forEach(formId => {
                const form = document.getElementById(formId);
                if (form && form.style.display === 'block') {
                    toggleForm(formId, false);
                    e.preventDefault();
                }
            });

            // Закрытие режима редактирования
            document.querySelectorAll('[id^="edit-"]').forEach(editElement => {
                if (editElement.style.display === 'block') {
                    const elementId = editElement.id.replace('edit-', '');
                    toggleEdit(elementId, false);
                    e.preventDefault();
                }
            });
        }
    });
}

// Инициализация
document.addEventListener('DOMContentLoaded', function () {
    setupAutoResizeTextareas();
    setupEscapeHandler();

    // Подтверждение действий
    document.querySelectorAll('[data-confirm]').forEach(element => {
        if (element.tagName === 'FORM') {
            element.addEventListener('submit', function (e) {
                if (!confirm(this.getAttribute('data-confirm'))) {
                    e.preventDefault();
                }
            });
        } else {
            element.addEventListener('click', function (e) {
                if (!confirm(this.getAttribute('data-confirm'))) {
                    e.preventDefault();
                }
            });
        }
    });
});

// Экспорт функций для использования в inline-скриптах
window.toggleForm = toggleForm;
window.toggleEdit = toggleEdit;
window.setupAutoResizeTextareas = setupAutoResizeTextareas;