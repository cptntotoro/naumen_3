// Mobile menu toggle
document.addEventListener('DOMContentLoaded', function () {
    const toggler = document.getElementById('navbarToggle');
    const navbar = document.getElementById('navbarMenu');

    if (toggler && navbar) {
        toggler.addEventListener('click', function () {
            navbar.classList.toggle('active');
            const icon = toggler.querySelector('i');
            icon.className = navbar.classList.contains('active') ? 'fas fa-times' : 'fas fa-bars';
        });

        // Close menu when clicking on links
        const navLinks = navbar.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.addEventListener('click', () => {
                navbar.classList.remove('active');
                toggler.querySelector('i').className = 'fas fa-bars';
            });
        });

        // Close menu when clicking outside
        document.addEventListener('click', function (event) {
            const isClickInsideNavbar = navbar.contains(event.target);
            const isClickInsideToggler = toggler.contains(event.target);

            if (!isClickInsideNavbar && !isClickInsideToggler && navbar.classList.contains('active')) {
                navbar.classList.remove('active');
                toggler.querySelector('i').className = 'fas fa-bars';
            }
        });
    }
});