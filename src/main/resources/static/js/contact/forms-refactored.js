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

    // Получаем текущее количество существующих полей
    const existingFields = container.querySelectorAll('.dynamic-field');
    const index = existingFields.length;

    // Клонируем шаблон
    const clone = template.cloneNode(true);
    clone.removeAttribute('id');
    clone.style.display = '';
    clone.classList.add('dynamic-field');

    // Обновляем индексы в именах полей
    const elements = clone.querySelectorAll('[name]');
    elements.forEach(el => {
        const name = el.getAttribute('name');
        if (name && name.includes('[INDEX]')) {
            el.setAttribute('name', name.replace('[INDEX]', `[${index}]`));
        }
    });

    container.appendChild(clone);
    const newField = container.lastElementChild;

    // Заполняем селекты данными
    populateSelectOptions(newField, type);

    // Прокручиваем к добавленному элементу
    setTimeout(() => {
        newField.scrollIntoView({behavior: 'smooth', block: 'nearest'});
    }, 10);

    // Анимация появления
    newField.style.opacity = '0';
    newField.style.transform = 'translateY(10px)';
    setTimeout(() => {
        newField.style.transition = 'all 0.3s ease';
        newField.style.opacity = '1';
        newField.style.transform = 'translateY(0)';
    }, 10);
}

// Функция для заполнения селектов
function populateSelectOptions(fieldElement, type) {
    // Глобальные переменные с данными (должны быть установлены в HTML)
    const globalData = window.contactFormData || {};

    switch (type) {
        case 'contactDetails':
            const detailTypeSelect = fieldElement.querySelector('select[name*="detailType"]');
            const detailLabelSelect = fieldElement.querySelector('select[name*="label"]');

            if (detailTypeSelect && globalData.detailTypes) {
                populateSelect(detailTypeSelect, globalData.detailTypes);
            }
            if (detailLabelSelect && globalData.detailLabels) {
                populateSelect(detailLabelSelect, globalData.detailLabels);
            }
            break;

        case 'socialProfiles':
            const platformSelect = fieldElement.querySelector('select[name*="platform"]');
            if (platformSelect && globalData.platforms) {
                populateSelect(platformSelect, globalData.platforms);
            }
            break;

        case 'companies':
            const companySelect = fieldElement.querySelector('select[name*="companyId"]');
            const jobTitleSelect = fieldElement.querySelector('select[name*="jobTitleId"]');

            if (companySelect && globalData.companies) {
                populateSelectWithIds(companySelect, globalData.companies);
            }
            if (jobTitleSelect && globalData.jobTitles) {
                populateSelectWithIds(jobTitleSelect, globalData.jobTitles);
            }
            break;

        case 'events':
            const eventTypeSelect = fieldElement.querySelector('select[name*="eventType"]');
            if (eventTypeSelect && globalData.eventTypes) {
                populateSelect(eventTypeSelect, globalData.eventTypes);
            }
            break;
    }
}

// Вспомогательные функции для заполнения селектов
function populateSelect(selectElement, options) {
    if (!selectElement || !options || options.length === 0) return;

    // Сохраняем выбранное значение
    const currentValue = selectElement.value;

    // Очищаем все опции кроме первой (placeholder)
    while (selectElement.options.length > 1) {
        selectElement.remove(1);
    }

    // Добавляем новые опции
    options.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option;
        opt.textContent = option;
        selectElement.appendChild(opt);
    });

    // Восстанавливаем выбранное значение
    if (currentValue) {
        selectElement.value = currentValue;
    }
}

function populateSelectWithIds(selectElement, items) {
    if (!selectElement || !items || items.length === 0) return;

    const currentValue = selectElement.value;

    while (selectElement.options.length > 1) {
        selectElement.remove(1);
    }

    items.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item.id;
        opt.textContent = item.name || item.title || item.value;
        selectElement.appendChild(opt);
    });

    if (currentValue) {
        selectElement.value = currentValue;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // Инициализация индексов
    initFormIndexes();

    // Сохраняем данные для динамического заполнения
    const scriptTag = document.querySelector('script[type="application/json"]#contact-form-data');
    if (scriptTag) {
        try {
            window.contactFormData = JSON.parse(scriptTag.textContent);
        } catch (e) {
            console.error('Error parsing contact form data:', e);
        }
    }

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