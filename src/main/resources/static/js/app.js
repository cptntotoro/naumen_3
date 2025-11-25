class ContactManager {
    constructor() {
        this.init();
    }

    init() {
        this.initFormValidation();
        this.initConfirmDialogs();
        this.initAutoSubmit();
    }

    initFormValidation() {
        const forms = document.querySelectorAll('.needs-validation');
        forms.forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }

    initConfirmDialogs() {
        document.querySelectorAll('form[data-confirm]').forEach(form => {
            form.addEventListener('submit', function (e) {
                if (!confirm(this.getAttribute('data-confirm'))) {
                    e.preventDefault();
                }
            });
        });
    }

    initAutoSubmit() {
        document.querySelectorAll('.auto-submit').forEach(element => {
            element.addEventListener('change', function () {
                this.form.submit();
            });
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new ContactManager();
});