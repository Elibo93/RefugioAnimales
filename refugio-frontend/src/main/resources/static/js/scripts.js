document.addEventListener('DOMContentLoaded', () => {
    // Lucide Icons
    if (window.lucide) {
        lucide.createIcons();
    }

    // Re-initialize Lucide Icons after HTMX swaps
    document.body.addEventListener('htmx:afterSettle', () => {
        if (window.lucide) {
            lucide.createIcons();
        }
    });

    // Initialize carousel
    initCarousel();

    // Auto-dismiss Toasts
    const toasts = document.querySelectorAll('.toast');
    if (toasts.length > 0) {
        setTimeout(() => {
            toasts.forEach(toast => {
                toast.style.transition = 'opacity 0.5s ease-out, transform 0.5s ease-out';
                toast.style.opacity = '0';
                toast.style.transform = 'translateY(-20px)';
                setTimeout(() => toast.remove(), 500);
            });
        }, 3000);
    }

    // Sidebar Logic
    const openSidebarBtn = document.getElementById('open-sidebar');
    const closeSidebarBtn = document.getElementById('close-sidebar');
    const sidebar = document.getElementById('app-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');

    if (openSidebarBtn && sidebar && sidebarOverlay) {
        const toggleSidebar = () => {
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active');
            document.body.style.overflow = sidebar.classList.contains('active') ? 'hidden' : '';
        };

        openSidebarBtn.addEventListener('click', toggleSidebar);
        if (closeSidebarBtn) closeSidebarBtn.addEventListener('click', toggleSidebar);
        sidebarOverlay.addEventListener('click', toggleSidebar);
    }

    // Dropdown Logic (for legacy or internal dropdowns)
    const dropdownTrigger = document.querySelector('.dropdown-trigger');
    const dropdownMenu = document.querySelector('.dropdown-menu');
    const dropdownContainer = document.querySelector('.dropdown-container');

    if (dropdownTrigger && dropdownMenu) {
        dropdownTrigger.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdownMenu.classList.toggle('active');
        });

        document.addEventListener('click', (e) => {
            if (dropdownContainer && !dropdownContainer.contains(e.target)) {
                dropdownMenu.classList.remove('active');
            }
        });
    }
});


function initCarousel() {
    const carousel = document.getElementById('story-carousel');
    if (!carousel) return;

    const slides = carousel.querySelectorAll('.story-card');
    if (slides.length <= 1) return;

    let currentSlide = 0;

    setInterval(() => {
        slides[currentSlide].style.display = 'none';
        slides[currentSlide].classList.remove('active');

        currentSlide = (currentSlide + 1) % slides.length;

        slides[currentSlide].style.display = 'flex';
        slides[currentSlide].classList.add('active');
    }, 5000);
}


