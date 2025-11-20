window.addContactDetail = function () {
    var template = document.getElementById('contactDetailTemplate').cloneNode(true);
    template.removeAttribute('id');
    template.style.display = '';
    var index = document.getElementById('contactDetailsContainer').children.length;
    var elements = template.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace('[INDEX]', '[' + index + ']');
    }
    document.getElementById('contactDetailsContainer').appendChild(template);
};

window.addSocialProfile = function () {
    var template = document.getElementById('socialProfileTemplate').cloneNode(true);
    template.removeAttribute('id');
    template.style.display = '';
    var index = document.getElementById('socialProfilesContainer').children.length;
    var elements = template.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace('[INDEX]', '[' + index + ']');
    }
    document.getElementById('socialProfilesContainer').appendChild(template);
};

window.addCompanyJobTitle = function () {
    var template = document.getElementById('companyJobTitleTemplate').cloneNode(true);
    template.removeAttribute('id');
    template.style.display = '';
    var index = document.getElementById('companyJobTitleContainer').children.length;
    var elements = template.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace('[INDEX]', '[' + index + ']');
    }
    document.getElementById('companyJobTitleContainer').appendChild(template);
};

window.addEvent = function () {
    var template = document.getElementById('eventTemplate').cloneNode(true);
    template.removeAttribute('id');
    template.style.display = '';
    var index = document.getElementById('eventsContainer').children.length;
    var elements = template.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace('[INDEX]', '[' + index + ']');
    }
    document.getElementById('eventsContainer').appendChild(template);
};

window.addNote = function () {
    var template = document.getElementById('noteTemplate').cloneNode(true);
    template.removeAttribute('id');
    template.style.display = '';
    var index = document.getElementById('notesContainer').children.length;
    var elements = template.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace('[INDEX]', '[' + index + ']');
    }
    document.getElementById('notesContainer').appendChild(template);
};

window.removeFormField = function (button) {
    const field = button.closest('.dynamic-field');
    field.remove();
};

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function () {
    // Избранное
    const favoriteCheckbox = document.getElementById('isFavorite');
    if (favoriteCheckbox) {
        updateFavoriteText(favoriteCheckbox);
    }

    // Основные контакты (для уже существующих на странице)
    const primaryCheckboxes = document.querySelectorAll('.primary-checkbox');
    primaryCheckboxes.forEach(checkbox => {
        updatePrimaryText(checkbox);
    });
});

// Функция для обновления текста основного контакта
function updatePrimaryText(checkbox) {
    const label = checkbox.nextElementSibling;
    const textElement = label.querySelector('.primary-text');

    if (checkbox.checked) {
        textElement.textContent = 'Сделать неосновным';
    } else {
        textElement.textContent = 'Сделать основным';
    }
}

// Функция для обновления текста избранного
function updateFavoriteText(checkbox) {
    const label = checkbox.nextElementSibling;
    const textElement = label.querySelector('.favorite-text');

    if (checkbox.checked) {
        textElement.textContent = 'Удалить из избранного';
    } else {
        textElement.textContent = 'Добавить в избранное';
    }
}

// Функция для переключения чекбокса при клике на label
function togglePrimaryCheckbox(label) {
    const checkbox = label.previousElementSibling;
    checkbox.checked = !checkbox.checked;
    updatePrimaryText(checkbox);
}

// Инициализация всех переключателей основного контакта
function initializePrimaryToggles() {
    const primaryToggles = document.querySelectorAll('.primary-toggle');

    primaryToggles.forEach(toggle => {
        const checkbox = toggle.querySelector('.primary-checkbox');

        if (checkbox) {
            // Обновляем текст при загрузке
            updatePrimaryText(checkbox);

            // Добавляем обработчик изменений
            checkbox.addEventListener('change', function () {
                updatePrimaryText(this);
            });
        }
    });
}