// Функция показа уведомлений (используется в details.html)
window.showNotification = function (message, type) {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type === 'success' ? 'success' : 'danger'}`;
    alert.textContent = message;
    alert.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        z-index: 10000;
        min-width: 300px;
        animation: slideInRight 0.3s ease-out;
    `;

    document.body.appendChild(alert);

    setTimeout(() => {
        alert.remove();
    }, 3000);
};

// Функция подтверждения действий
window.confirmAction = function (message, callback) {
    if (confirm(message)) {
        callback();
    }
};

// Функция для показа/скрытия элементов с анимацией
window.toggleElement = function (elementId, show) {
    const element = document.getElementById(elementId);
    if (!element) return;

    if (show === undefined) {
        show = element.style.display === 'none';
    }

    if (show) {
        element.style.display = 'block';
        element.style.opacity = '0';
        element.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            element.style.transition = 'all 0.3s ease';
            element.style.opacity = '1';
            element.style.transform = 'translateY(0)';
        }, 10);
    } else {
        element.style.opacity = '0';
        element.style.transform = 'translateY(-10px)';
        setTimeout(() => {
            element.style.display = 'none';
        }, 300);
    }
};