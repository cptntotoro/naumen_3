window.addContactDetail = function () {
    var template = document.getElementById('contactDetailTemplate');
    if (!template) {
        console.warn('contactDetailTemplate not found');
        return;
    }

    var container = document.getElementById('contactDetailsContainer');
    if (!container) {
        console.warn('contactDetailsContainer not found');
        return;
    }

    var clone = template.cloneNode(true);
    clone.removeAttribute('id');
    clone.style.display = '';
    var index = container.children.length;
    var elements = clone.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace(/\[INDEX\]/g, '[' + index + ']');
    }
    container.appendChild(clone);
};

window.addSocialProfile = function () {
    var template = document.getElementById('socialProfileTemplate');
    if (!template) {
        console.warn('socialProfileTemplate not found');
        return;
    }

    var container = document.getElementById('socialProfilesContainer');
    if (!container) {
        console.warn('socialProfilesContainer not found');
        return;
    }

    var clone = template.cloneNode(true);
    clone.removeAttribute('id');
    clone.style.display = '';
    var index = container.children.length;
    var elements = clone.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace(/\[INDEX\]/g, '[' + index + ']');
    }
    container.appendChild(clone);
};

window.addCompanyJobTitle = function () {
    var template = document.getElementById('companyJobTitleTemplate');
    if (!template) {
        console.warn('companyJobTitleTemplate not found');
        return;
    }

    var container = document.getElementById('companyJobTitleContainer');
    if (!container) {
        console.warn('companyJobTitleContainer not found');
        return;
    }

    var clone = template.cloneNode(true);
    clone.removeAttribute('id');
    clone.style.display = '';
    var index = container.children.length;
    var elements = clone.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace(/\[INDEX\]/g, '[' + index + ']');
    }
    container.appendChild(clone);
};

window.addEvent = function () {
    var template = document.getElementById('eventTemplate');
    if (!template) {
        console.warn('eventTemplate not found');
        return;
    }

    var container = document.getElementById('eventsContainer');
    if (!container) {
        console.warn('eventsContainer not found');
        return;
    }

    var clone = template.cloneNode(true);
    clone.removeAttribute('id');
    clone.style.display = '';
    var index = container.children.length;
    var elements = clone.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace(/\[INDEX\]/g, '[' + index + ']');
    }
    container.appendChild(clone);
};

window.addNote = function () {
    var template = document.getElementById('noteTemplate');
    if (!template) {
        console.warn('noteTemplate not found');
        return;
    }

    var container = document.getElementById('notesContainer');
    if (!container) {
        console.warn('notesContainer not found');
        return;
    }

    var clone = template.cloneNode(true);
    clone.removeAttribute('id');
    clone.style.display = '';
    var index = container.children.length;
    var elements = clone.querySelectorAll('[name]');
    for (var i = 0; i < elements.length; i++) {
        elements[i].name = elements[i].name.replace(/\[INDEX\]/g, '[' + index + ']');
    }
    container.appendChild(clone);
};

window.removeFormField = function (button) {
    const field = button.closest('.dynamic-field');
    if (field) {
        field.remove();
    }
};

// Функции для работы с чекбоксами
function updatePrimaryText(checkbox) {
    const label = checkbox.closest('.primary-toggle').querySelector('.primary-label');
    const textElement = label.querySelector('.primary-text');

    if (textElement) {
        if (checkbox.checked) {
            textElement.textContent = 'Сделать неосновным';
        } else {
            textElement.textContent = 'Сделать основным';
        }
    }
}

function updateFavoriteText(checkbox) {
    const label = checkbox.closest('.favorite-toggle').querySelector('.favorite-label');
    const textElement = label.querySelector('.favorite-text');

    if (textElement) {
        if (checkbox.checked) {
            textElement.textContent = 'Удалить из избранного';
        } else {
            textElement.textContent = 'Добавить в избранное';
        }
    }
}

function updateCurrentText(checkbox) {
    const icon = checkbox.closest('.current-job-toggle').querySelector('.current-icon');
    if (icon) {
        icon.className = checkbox.checked
            ? 'current-icon fas fa-check-circle text-success'
            : 'current-icon far fa-circle';
    }
}

function togglePrimaryCheckbox(label) {
    const checkbox = label.closest('.primary-toggle').querySelector('.primary-checkbox');
    if (checkbox) {
        checkbox.checked = !checkbox.checked;
        updatePrimaryText(checkbox);
    }
}

function toggleCurrentCheckbox(label) {
    const checkbox = label.closest('.current-job-toggle').querySelector('.current-checkbox');
    if (!checkbox) return;

    // Если включаем текущее место работы, выключаем все остальные
    if (!checkbox.checked) {
        document.querySelectorAll('.current-checkbox').forEach(otherCheckbox => {
            if (otherCheckbox !== checkbox) {
                otherCheckbox.checked = false;
                updateCurrentText(otherCheckbox);
            }
        });
    }

    checkbox.checked = !checkbox.checked;
    updateCurrentText(checkbox);
}

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

    // Текущие места работы
    const currentCheckboxes = document.querySelectorAll('.current-checkbox');
    currentCheckboxes.forEach(checkbox => {
        updateCurrentText(checkbox);
    });
});

// Глобальные функции для HTML
window.updatePrimaryText = updatePrimaryText;
window.updateFavoriteText = updateFavoriteText;
window.updateCurrentText = updateCurrentText;
window.togglePrimaryCheckbox = togglePrimaryCheckbox;
window.toggleCurrentCheckbox = toggleCurrentCheckbox;