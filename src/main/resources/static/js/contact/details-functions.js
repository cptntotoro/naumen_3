// Все функции для работы со страницей details

// Функции для работы с компаниями
function toggleCompanyForm() {
    const container = document.getElementById('companyFormContainer');
    if (!container) return;

    if (container.style.display === 'block') {
        hideCompanyForm();
    } else {
        container.style.display = 'block';
        container.style.opacity = '0';
        container.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        }, 10);
        container.querySelector('select')?.focus();
    }
}

function hideCompanyForm() {
    const container = document.getElementById('companyFormContainer');
    if (!container) return;

    container.style.opacity = '0';
    container.style.transform = 'translateY(-10px)';
    setTimeout(() => {
        container.style.display = 'none';
        document.getElementById('companyForm')?.reset();
    }, 300);
}

function editCompany(id) {
    const item = document.querySelector(`.company-item[data-id="${id}"]`);
    if (item) {
        item.querySelector('.company-view').style.display = 'none';
        item.querySelector('.company-edit-form').style.display = 'block';
    }
}

function cancelEditCompany(id) {
    const item = document.querySelector(`.company-item[data-id="${id}"]`);
    if (item) {
        item.querySelector('.company-view').style.display = 'flex';
        item.querySelector('.company-edit-form').style.display = 'none';
    }
}

// Функции для работы с социальными профилями
function toggleSocialProfileForm() {
    const formContainer = document.getElementById('socialFormContainer');
    const platformSelect = document.getElementById('platform');

    if (!formContainer) return;

    if (formContainer.style.display === 'none') {
        formContainer.style.display = 'block';
        formContainer.style.opacity = '0';
        formContainer.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            formContainer.style.transition = 'all 0.3s ease';
            formContainer.style.opacity = '1';
            formContainer.style.transform = 'translateY(0)';

            if (platformSelect) {
                setTimeout(() => {
                    platformSelect.focus();
                }, 300);
            }
        }, 10);
    } else {
        hideSocialProfileForm();
    }
}

function hideSocialProfileForm() {
    const formContainer = document.getElementById('socialFormContainer');
    const socialForm = document.getElementById('socialForm');

    if (formContainer) {
        formContainer.style.opacity = '0';
        formContainer.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            formContainer.style.display = 'none';
            if (socialForm) {
                socialForm.reset();
                document.getElementById('customPlatformContainer').style.display = 'none';
            }
        }, 300);
    }
}

function toggleCustomPlatformName() {
    const platform = document.getElementById('platform');
    const customContainer = document.getElementById('customPlatformContainer');

    if (platform && customContainer) {
        customContainer.style.display = platform.value === 'CUSTOM' ? 'block' : 'none';
        if (platform.value === 'CUSTOM') {
            document.getElementById('customPlatformName').focus();
        }
    }
}

function toggleEditCustomPlatformName(profileId) {
    const platform = document.querySelector(`#edit-social-form-${profileId} select[name="platform"]`);
    const customContainer = document.getElementById(`edit-custom-platform-container-${profileId}`);

    if (platform && customContainer) {
        customContainer.style.display = platform.value === 'CUSTOM' ? 'block' : 'none';
    }
}

function editSocialProfile(profileId) {
    const viewElement = document.getElementById(`social-profile-view-${profileId}`);
    const editForm = document.getElementById(`edit-social-form-${profileId}`);

    if (viewElement && editForm) {
        viewElement.style.display = 'none';
        editForm.style.display = 'block';

        const firstInput = editForm.querySelector('select[name="platform"]');
        if (firstInput) {
            setTimeout(() => {
                firstInput.focus();
            }, 10);
        }
    }
}

function cancelSocialProfileEdit(profileId) {
    const viewElement = document.getElementById(`social-profile-view-${profileId}`);
    const editForm = document.getElementById(`edit-social-form-${profileId}`);

    if (viewElement && editForm) {
        viewElement.style.display = 'flex';
        editForm.style.display = 'none';
        editForm.reset();
    }
}

// Функции для работы с событиями
function toggleEventForm() {
    const formContainer = document.getElementById('eventFormContainer');
    const eventSelect = document.getElementById('eventType');

    if (!formContainer) return;

    if (formContainer.style.display === 'none') {
        formContainer.style.display = 'block';
        formContainer.style.opacity = '0';
        formContainer.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            formContainer.style.transition = 'all 0.3s ease';
            formContainer.style.opacity = '1';
            formContainer.style.transform = 'translateY(0)';

            const today = new Date().toISOString().split('T')[0];
            const eventDate = document.getElementById('eventDate');
            if (eventDate) eventDate.value = today;

            if (eventSelect) {
                setTimeout(() => {
                    eventSelect.focus();
                }, 300);
            }
        }, 10);
    } else {
        hideEventForm();
    }
}

function hideEventForm() {
    const formContainer = document.getElementById('eventFormContainer');
    const eventForm = document.getElementById('eventForm');

    if (formContainer) {
        formContainer.style.opacity = '0';
        formContainer.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            formContainer.style.display = 'none';
            if (eventForm) {
                eventForm.reset();
                document.getElementById('customEventNameContainer').style.display = 'none';
            }
        }, 300);
    }
}

function toggleCustomEventName() {
    const eventType = document.getElementById('eventType');
    const customContainer = document.getElementById('customEventNameContainer');

    if (eventType && customContainer) {
        if (eventType.value === 'CUSTOM') {
            customContainer.style.display = 'block';
            document.getElementById('customEventName').focus();
        } else {
            customContainer.style.display = 'none';
            document.getElementById('customEventName').value = '';
        }
    }
}

function toggleEditCustomEventName(eventId) {
    const eventType = document.querySelector(`#edit-event-form-${eventId} select[name="eventType"]`);
    const customContainer = document.getElementById(`edit-custom-container-${eventId}`);

    if (eventType && customContainer) {
        if (eventType.value === 'CUSTOM') {
            customContainer.style.display = 'block';
        } else {
            customContainer.style.display = 'none';
        }
    }
}

function editEvent(eventId) {
    const viewElement = document.getElementById(`event-view-${eventId}`);
    const editForm = document.getElementById(`edit-event-form-${eventId}`);

    if (viewElement && editForm) {
        viewElement.style.display = 'none';
        editForm.style.display = 'block';

        const firstInput = editForm.querySelector('select[name="eventType"]');
        if (firstInput) {
            setTimeout(() => {
                firstInput.focus();
            }, 10);
        }
    }
}

function cancelEventEdit(eventId) {
    const viewElement = document.getElementById(`event-view-${eventId}`);
    const editForm = document.getElementById(`edit-event-form-${eventId}`);

    if (viewElement && editForm) {
        viewElement.style.display = 'flex';
        editForm.style.display = 'none';
        editForm.reset();
    }
}

// Функции для работы с заметками
function toggleNoteForm() {
    const formContainer = document.getElementById('noteFormContainer');
    const noteTextarea = document.getElementById('noteContent');

    if (!formContainer) return;

    if (formContainer.style.display === 'none') {
        formContainer.style.display = 'block';
        formContainer.style.opacity = '0';
        formContainer.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            formContainer.style.transition = 'all 0.3s ease';
            formContainer.style.opacity = '1';
            formContainer.style.transform = 'translateY(0)';
        }, 10);

        if (noteTextarea) {
            setTimeout(() => {
                noteTextarea.focus();
            }, 300);
        }
    } else {
        hideNoteForm();
    }
}

function hideNoteForm() {
    const formContainer = document.getElementById('noteFormContainer');
    const noteForm = document.getElementById('noteForm');
    const noteTextarea = document.getElementById('noteContent');

    if (formContainer) {
        formContainer.style.opacity = '0';
        formContainer.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            formContainer.style.display = 'none';
            if (noteForm) {
                noteForm.reset();
            }
            if (noteTextarea) {
                noteTextarea.style.height = 'auto';
            }
        }, 300);
    }
}

function editNote(noteId) {
    const viewElement = document.getElementById(`view-${noteId}`);
    const editForm = document.getElementById(`edit-form-${noteId}`);

    if (viewElement && editForm) {
        viewElement.style.display = 'none';
        editForm.style.display = 'block';

        const textarea = editForm.querySelector('.note-edit-textarea');
        if (textarea) {
            setTimeout(() => {
                textarea.focus();
                textarea.style.height = 'auto';
                textarea.style.height = (textarea.scrollHeight) + 'px';
            }, 10);
        }
    }
}

function cancelEdit(noteId) {
    const viewElement = document.getElementById(`view-${noteId}`);
    const editForm = document.getElementById(`edit-form-${noteId}`);

    if (viewElement && editForm) {
        viewElement.style.display = 'block';
        editForm.style.display = 'none';
        editForm.reset();
    }
}

// Экспорт функций в глобальную область видимости
window.toggleCompanyForm = toggleCompanyForm;
window.hideCompanyForm = hideCompanyForm;
window.editCompany = editCompany;
window.cancelEditCompany = cancelEditCompany;

window.toggleSocialProfileForm = toggleSocialProfileForm;
window.hideSocialProfileForm = hideSocialProfileForm;
window.toggleCustomPlatformName = toggleCustomPlatformName;
window.toggleEditCustomPlatformName = toggleEditCustomPlatformName;
window.editSocialProfile = editSocialProfile;
window.cancelSocialProfileEdit = cancelSocialProfileEdit;

window.toggleEventForm = toggleEventForm;
window.hideEventForm = hideEventForm;
window.toggleCustomEventName = toggleCustomEventName;
window.toggleEditCustomEventName = toggleEditCustomEventName;
window.editEvent = editEvent;
window.cancelEventEdit = cancelEventEdit;

window.toggleNoteForm = toggleNoteForm;
window.hideNoteForm = hideNoteForm;
window.editNote = editNote;
window.cancelEdit = cancelEdit;