const initApp = () => {
    // 1. Listeners Globales (Solo se registran una vez)
    
    // Auto-ocultar sugerencias de usuario al hacer clic fuera
    document.addEventListener('click', (e) => {
        const suggestions = document.getElementById('user-suggestions');
        const searchInput = document.getElementById('user-search');
        if (suggestions && searchInput && !suggestions.contains(e.target) && e.target !== searchInput) {
            suggestions.innerHTML = '';
        }
        
        // Cerrar dropdowns al hacer clic fuera
        const dropdownContainer = document.querySelector('.dropdown-container');
        const dropdownMenu = document.querySelector('.dropdown-menu');
        if (dropdownMenu && dropdownContainer && !dropdownContainer.contains(e.target)) {
            dropdownMenu.classList.remove('active');
        }
    });

    // 2. Escuchar eventos de HTMX para re-inicializar componentes tras swaps
    document.body.addEventListener('htmx:afterSettle', () => {
        // Para componentes que NO usan delegación (como carruseles o inicializaciones de Lucide)
        if (window.lucide) lucide.createIcons();
        
        // Re-inicializar lógica que dependa de elementos específicos recién llegados
        const donationSelect = document.getElementById('donation-type-select');
        if (donationSelect && !donationSelect.dataset.processed) {
            handleTypeChange();
            donationSelect.dataset.processed = "true";
        }
    });

    // 3. DELEGACIÓN DE EVENTOS (Solución definitiva para Sidebar y Dropdowns)
    
    // Lógica del Sidebar (Menú lateral)
    document.addEventListener('click', (e) => {
        const openBtn = e.target.closest('#open-sidebar');
        const closeBtn = e.target.closest('#close-sidebar');
        const overlay = e.target.closest('#sidebar-overlay');
        const sidebar = document.getElementById('app-sidebar');
        const sidebarOverlay = document.getElementById('sidebar-overlay');

        if ((openBtn || closeBtn || overlay) && sidebar && sidebarOverlay) {
            if (openBtn) e.preventDefault();
            
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active');
            document.body.style.overflow = sidebar.classList.contains('active') ? 'hidden' : '';
        }

        // Lógica de Desplegables (Dropdowns)
        const dropdownTrigger = e.target.closest('.dropdown-trigger');
        if (dropdownTrigger) {
            e.stopPropagation();
            const dropdownMenu = dropdownTrigger.nextElementSibling || document.querySelector('.dropdown-menu');
            if (dropdownMenu) dropdownMenu.classList.toggle('active');
        }

        // Lógica de Submenús en el Sidebar (Delegada para resistir htmx:historyRestore)
        const submenuToggle = e.target.closest('.submenu-toggle');
        if (submenuToggle) {
            e.preventDefault();
            const group = submenuToggle.closest('.sidebar-item-group');
            if (group) {
                const submenu = group.querySelector('.sidebar-submenu');
                group.classList.toggle('open');
                if (submenu) submenu.classList.toggle('open');
            }
        }

        // Lógica para alternar formulario de donación para el Administrador
        const btnAdminDonar = e.target.closest('#btn-admin-donar');
        if (btnAdminDonar) {
            const formContainer = document.getElementById('donacion-form-container');
            if (formContainer) {
                if (formContainer.style.display === 'none' || formContainer.style.display === '') {
                    formContainer.style.display = 'block';
                    setTimeout(() => {
                        formContainer.style.opacity = '1';
                        formContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }, 50);
                    btnAdminDonar.style.background = '#64748b'; // Color Slate
                    btnAdminDonar.innerHTML = '<i data-lucide="heart-off" style="width: 18px;"></i><span>Ocultar Formulario</span>';
                } else {
                    formContainer.style.opacity = '0';
                    setTimeout(() => {
                        formContainer.style.display = 'none';
                    }, 400);
                    btnAdminDonar.style.background = 'var(--primary)';
                    btnAdminDonar.innerHTML = '<i data-lucide="heart" style="width: 18px; fill: white;"></i><span>Registrar Aportación</span>';
                }
                if (window.lucide) {
                    lucide.createIcons();
                }
            }
        }
    });

    // Inicialización global de componentes al cargar por primera vez
    refreshDynamicComponents();
};

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}

// === FUNCIONES GLOBALES ===

// Actualización general de componentes dinámicos (Para inicializaciones puntuales)
function refreshDynamicComponents() {
    if (window.lucide) {
        lucide.createIcons();
    }

    // Lógica de Submenús eliminada (ahora está delegada globalmente)

    // 4. Inicialización automática de cuadrículas con paginación
    if (document.getElementById('animal-grid-admin')) initPagination('animal-grid-admin');
    if (document.getElementById('animal-grid-cards')) initPagination('animal-grid-cards');
    if (document.getElementById('historial-grid')) initPagination('historial-grid');
    
    // 5. Inicialización de Formulario de Donación
    if (document.getElementById('donation-type-select')) {
        handleTypeChange();
        // Inicializar recurrencia visual si no hay valor previo
        const freqInput = document.getElementById('frecuencia-input');
        const freq = (freqInput && freqInput.value) ? freqInput.value.toLowerCase() : 'unica';
        setRecurrence(freq);
    }

    // Inicialización automática de carruseles
    if (document.querySelectorAll('.story-card').length > 0) startCarouselAutoPlay();

    // 6. Lógica de Toasts (Auto-ocultar después de 5 segundos)
    document.querySelectorAll('.toast').forEach(toast => {
        if (!toast.dataset.processed) {
            // Añadimos una clase para la animación de salida si no la tiene
            setTimeout(() => {
                toast.style.opacity = '0';
                toast.style.transform = 'translateY(-20px)';
                toast.style.transition = 'all 0.5s ease';
                setTimeout(() => {
                    toast.remove();
                }, 500);
            }, 5000);
            toast.dataset.processed = "true";
        }
    });
}
