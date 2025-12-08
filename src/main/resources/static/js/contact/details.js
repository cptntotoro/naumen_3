// Используем общие функции
function toggleCompanyForm() {
    toggleForm('companyFormContainer');
}

function toggleSocialProfileForm() {
    toggleForm('socialFormContainer');
}

function toggleEventForm() {
    toggleForm('eventFormContainer');
}

function toggleNoteForm() {
    toggleForm('noteFormContainer');
}

// Специфичные функции
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