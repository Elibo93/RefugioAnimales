document.addEventListener('DOMContentLoaded', () => {
    // Lucide Icons
    if (window.lucide) {
        lucide.createIcons();
    }

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


