// Унифицированные функции для работы с формами

window.contactFormIndexes = {
    contactDetails: 0,
    socialProfiles: 0,
    companies: 0,
    events: 0,
    notes: 0
};

// Инициализация индексов из существующих полей
function initFormIndexes() {
    const containers = ['contactDetails', 'socialProfiles', 'companies', 'events', 'notes'];
    containers.forEach(type => {
        const container = document.getElementById(`${type}Container`);
        if (container) {
            // Считаем только динамические поля
            const dynamicFields = container.querySelectorAll('.dynamic-field');
            window.contactFormIndexes[type] = dynamicFields.length;
        }
    });
}

// Универсальная функция добавления поля
function addDynamicField(type, templateId, containerId, useCloning = false) {
    const template = document.getElementById(templateId);
    const container = document.getElementById(containerId);

    if (!template || !container) {
        console.error(`Template ${templateId} or container ${containerId} not found`);
        return;
    }

    const index = window.contactFormIndexes[type]++;

    if (useCloning) {
        // Старый подход для совместимости
        const clone = template.cloneNode(true);
        clone.removeAttribute('id');
        clone.style.display = '';

        const elements = clone.querySelectorAll('[name]');
        elements.forEach(el => {
            el.name = el.name.replace(/\[INDEX\]/g, `[${index}]`);
        });

        container.appendChild(clone);
    } else {
        // Новый подход с подстановкой
        const html = template.innerHTML.replace(/INDEX/g, index);
        const div = document.createElement('div');
        div.className = 'dynamic-field';
        div.innerHTML = html;
        container.appendChild(div);
    }

    // Прокручиваем к добавленному элементу
    const addedElement = container.lastElementChild;
    setTimeout(() => {
        addedElement.scrollIntoView({behavior: 'smooth', block: 'nearest'});
    }, 10);

    // Анимация появления
    if (!useCloning) {
        addedElement.style.opacity = '0';
        addedElement.style.transform = 'translateY(10px)';
        setTimeout(() => {
            addedElement.style.transition = 'all 0.3s ease';
            addedElement.style.opacity = '1';
            addedElement.style.transform = 'translateY(0)';
        }, 10);
    }
}

// Специфичные функции для каждого типа поля
window.addContactDetail = function () {
    addDynamicField('contactDetails', 'contactDetailTemplate', 'contactDetailsContainer');
};

window.addSocialProfile = function () {
    addDynamicField('socialProfiles', 'socialProfileTemplate', 'socialProfilesContainer');
};

window.addCompanyJobTitle = function () {
    addDynamicField('companies', 'companyJobTitleTemplate', 'companyJobTitleContainer');
};

window.addEvent = function () {
    addDynamicField('events', 'eventTemplate', 'eventsContainer');
};

window.addNote = function () {
    addDynamicField('notes', 'noteTemplate', 'notesContainer');
};

// Удаление поля
window.removeFormField = function (button) {
    const field = button.closest('.dynamic-field');
    if (field) {
        field.style.opacity = '0';
        field.style.transform = 'translateY(10px)';
        setTimeout(() => {
            field.remove();
        }, 300);
    }
};

// Общие функции для чекбоксов
function updateCheckboxText(checkbox, config) {
    const label = checkbox.closest(config.containerClass);
    if (label) {
        const icon = label.querySelector(config.iconSelector);
        const text = label.querySelector(config.textSelector);

        if (checkbox.checked) {
            if (icon) icon.className = config.checkedIconClass;
            if (text) text.textContent = config.checkedText;
        } else {
            if (icon) icon.className = config.uncheckedIconClass;
            if (text) text.textContent = config.uncheckedText;
        }
    }
}

// Конфигурация для разных типов чекбоксов
const checkboxConfigs = {
    favorite: {
        containerClass: '.favorite-toggle',
        iconSelector: '.favorite-icon',
        textSelector: '.favorite-text',
        checkedIconClass: 'favorite-icon fas fa-star text-warning',
        uncheckedIconClass: 'favorite-icon far fa-star',
        checkedText: 'В избранном',
        uncheckedText: 'Добавить в избранное'
    },
    primary: {
        containerClass: '.primary-toggle',
        iconSelector: '.primary-icon',
        textSelector: '.primary-text',
        checkedIconClass: 'primary-icon fas fa-check-circle text-success',
        uncheckedIconClass: 'primary-icon far fa-circle',
        checkedText: 'Основной',
        uncheckedText: 'Сделать основным'
    },
    current: {
        containerClass: '.current-job-toggle',
        iconSelector: '.current-icon',
        textSelector: '.current-text',
        checkedIconClass: 'current-icon fas fa-check-circle text-success',
        uncheckedIconClass: 'current-icon far fa-circle',
        checkedText: 'Текущее место работы',
        uncheckedText: 'Текущее место работы'
    }
};

// Функции обновления текста
window.updateFavoriteText = function (checkbox) {
    updateCheckboxText(checkbox, checkboxConfigs.favorite);
};

window.updatePrimaryText = function (checkbox) {
    updateCheckboxText(checkbox, checkboxConfigs.primary);
};

window.updateCurrentText = function (checkbox) {
    updateCheckboxText(checkbox, checkboxConfigs.current);
};

// Функции переключения
function toggleCheckbox(label, config) {
    const container = label.closest(config.containerClass);
    const checkbox = container.querySelector('input[type="checkbox"]');

    if (checkbox) {
        checkbox.checked = !checkbox.checked;

        // Для primary checkbox снимаем отметку с других
        if (config.type === 'primary' && checkbox.checked) {
            const formContainer = checkbox.closest('.dynamic-fields-container');
            if (formContainer) {
                formContainer.querySelectorAll('.primary-checkbox').forEach(other => {
                    if (other !== checkbox) {
                        other.checked = false;
                        updatePrimaryText(other);
                    }
                });
            }
        }

        updateCheckboxText(checkbox, config);
    }
}

window.toggleFavoriteCheckbox = function (label) {
    toggleCheckbox(label, {...checkboxConfigs.favorite, type: 'favorite'});
};

window.togglePrimaryCheckbox = function (label) {
    toggleCheckbox(label, {...checkboxConfigs.primary, type: 'primary'});
};

window.toggleCurrentCheckbox = function (label) {
    toggleCheckbox(label, {...checkboxConfigs.current, type: 'current'});
};

// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', function () {
    // Инициализация индексов
    initFormIndexes();

    // Обновление текста чекбоксов
    document.querySelectorAll('.favorite-checkbox').forEach(cb => updateFavoriteText(cb));
    document.querySelectorAll('.primary-checkbox').forEach(cb => updatePrimaryText(cb));
    document.querySelectorAll('.current-checkbox').forEach(cb => updateCurrentText(cb));

    // Валидация формы
    const form = document.querySelector('form.needs-validation');
    if (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    }
});

// Экспорт для использования в других скриптах
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        addDynamicField,
        removeFormField,
        updateFavoriteText,
        updatePrimaryText,
        updateCurrentText,
        toggleFavoriteCheckbox,
        togglePrimaryCheckbox,
        toggleCurrentCheckbox
    };
}