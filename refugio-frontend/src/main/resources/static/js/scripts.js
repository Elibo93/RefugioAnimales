document.addEventListener('DOMContentLoaded', () => {
    // Iconos de Lucide
    if (window.lucide) {
        lucide.createIcons();
    }

    // Reinicializar componentes dinámicos tras intercambios de HTMX
    document.body.addEventListener('htmx:afterSettle', () => {
        refreshDynamicComponents();
    });

    // Carga inicial
    refreshDynamicComponents();

    // Auto-cierre de notificaciones (Toasts)
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

    // Lógica del Sidebar (Menú lateral)
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

    // Lógica de Desplegables (Dropdowns)
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

    // Lógica de Submenús en el Sidebar
    const submenuToggles = document.querySelectorAll('.submenu-toggle');
    submenuToggles.forEach(toggle => {
        toggle.addEventListener('click', () => {
            const group = toggle.closest('.sidebar-item-group');
            const submenu = group.querySelector('.sidebar-submenu');
            
            group.classList.toggle('open');
            if (submenu) {
                submenu.classList.toggle('open');
            }
        });
    });
});



// === FUNCIONES GLOBALES ===

// Actualización general de componentes dinámicos
function refreshDynamicComponents() {
    if (window.lucide) lucide.createIcons();
    
    // Inicialización automática de cuadrículas con paginación
    if (document.getElementById('animal-grid-admin')) initPagination('animal-grid-admin');
    if (document.getElementById('animal-grid-cards')) initPagination('animal-grid-cards');
    if (document.getElementById('historial-grid')) initPagination('historial-grid');
    
    // Inicialización automática de carruseles
    if (document.querySelectorAll('.story-card').length > 0) startCarouselAutoPlay();
}

// Lógica de Modales
function closeModal() {
    const modalHost = document.getElementById('modal-host');
    if (modalHost) {
        modalHost.innerHTML = '';
        document.body.classList.remove('modal-open');
    }
}

// Lógica de Carrusel
let carouselInterval = null;
let isCarouselMoving = false;

function startCarouselAutoPlay() {
    if (carouselInterval) clearInterval(carouselInterval);
    if (document.querySelectorAll('.story-card').length > 0) {
        carouselInterval = setInterval(() => moveCarousel(1, true), 5000);
    }
}

function moveCarousel(direction, isAuto = false) {
    if (isCarouselMoving && !isAuto) return;
    const items = document.querySelectorAll('.story-card');
    if (!items.length) return;
    isCarouselMoving = true;
    if (!isAuto) startCarouselAutoPlay();
    let currentItem = -1;
    items.forEach((item, index) => {
        if (item.classList.contains('active') || item.style.display === 'flex') {
            currentItem = index;
        }
    });
    if (currentItem === -1) currentItem = 0;
    items.forEach(item => {
        item.style.display = 'none';
        item.classList.remove('active');
    });
    currentItem = (currentItem + direction + items.length) % items.length;
    const nextItem = items[currentItem];
    nextItem.style.display = 'flex';
    void nextItem.offsetWidth; 
    nextItem.classList.add('active');
    setTimeout(() => { isCarouselMoving = false; }, 400);
    if (window.lucide) lucide.createIcons();
}

// Lógica de Paginación
function initPagination(gridId, itemsPerPage = 8) {
    const runInit = () => {
        const grid = document.getElementById(gridId);
        if (!grid) return;
        const items = Array.from(grid.querySelectorAll('.animal-row-item, .animal-card-item, .list-item'));
        if (!items.length) return;
        const totalPages = Math.ceil(items.length / itemsPerPage);
        let currentPage = 1;
        const controls = document.getElementById('pagination-controls');
        const prevBtn = document.getElementById('prevPage');
        const nextBtn = document.getElementById('nextPage');
        const pageNumSpan = document.getElementById('currentPageNum');
        const totalPagesSpan = document.getElementById('totalPagesNum');
        if (totalPagesSpan) totalPagesSpan.textContent = totalPages;
        if (items.length <= itemsPerPage) {
            if (controls) controls.style.display = 'none';
            items.forEach(i => i.style.display = (grid.tagName === 'TBODY' ? 'table-row' : 'block'));
            return;
        } else if (controls) {
            controls.style.display = 'flex';
        }
        function showPage(page) {
            currentPage = page;
            const start = (page - 1) * itemsPerPage;
            const end = start + itemsPerPage;
            items.forEach((item, index) => {
                const displayStyle = (grid.tagName === 'TBODY' ? 'table-row' : 'block');
                item.style.display = (index >= start && index < end) ? displayStyle : 'none';
            });
            if (pageNumSpan) pageNumSpan.textContent = page;
            if (prevBtn) prevBtn.disabled = (page === 1);
            if (nextBtn) nextBtn.disabled = (page === totalPages);
            if (window.lucide) lucide.createIcons();
        }
        if (prevBtn) prevBtn.onclick = () => { if (currentPage > 1) showPage(currentPage - 1); };
        if (nextBtn) nextBtn.onclick = () => { if (currentPage < totalPages) showPage(currentPage + 1); };
        showPage(1);
    };
    setTimeout(runInit, 100);
}

// Lógica de Filtrado de elementos
function filterListItems(filterClass, itemClass, status, dataAttr = 'status') {
    document.querySelectorAll('.' + filterClass).forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-filter') === status);
    });
    const items = document.querySelectorAll('.' + itemClass);
    items.forEach(item => {
        if (status === 'ALL' || item.getAttribute('data-' + dataAttr) === status) {
            item.style.display = 'flex';
        } else {
            item.style.display = 'none';
        }
    });
}

// Filtrado por prioridad (Tareas)
function filterByPriority(priority, btn) {
    if (btn) {
        const container = btn.parentElement;
        container.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    }
    const rows = document.querySelectorAll('.task-row');
    rows.forEach(row => {
        const rowPriority = row.getAttribute('data-priority');
        if (priority === 'ALL' || rowPriority === priority) {
            row.style.display = 'table-row';
        } else {
            row.style.display = 'none';
        }
    });
}

// Lógica del Formulario de Donaciones
function handleTypeChange() {
    const typeSelect = document.getElementById('donation-type-select');
    if (!typeSelect) return;
    const type = typeSelect.value;
    const panelDineroExtra = document.getElementById('panel-dinero-extra');
    const panelUnificado = document.getElementById('panel-unificado');
    const actions = document.getElementById('form-actions');
    const labelCant = document.getElementById('label-cantidad');
    const iconCant = document.getElementById('icon-cantidad');
    const inputCant = document.getElementById('input-cantidad');
    const labelDesc = document.getElementById('label-descripcion');
    const inputDesc = document.getElementById('input-descripcion');

    if (panelDineroExtra) panelDineroExtra.style.display = 'none';
    if (panelUnificado) panelUnificado.style.display = 'block';
    if (actions) actions.style.display = 'flex';

    if (inputCant) {
        inputCant.step = "0.01";
        inputCant.required = true;
    }
    if (inputDesc) inputDesc.required = (type !== 'DINERO');

    if (type === 'DINERO') {
        if (panelDineroExtra) panelDineroExtra.style.display = 'block';
        if (labelCant) labelCant.textContent = 'Importe de la donación (€)';
        if (iconCant) iconCant.setAttribute('data-lucide', 'euro');
        if (inputCant) inputCant.placeholder = '0.00';
        if (labelDesc) labelDesc.textContent = 'Mensaje o dedicatoria (opcional)';
        if (inputDesc) inputDesc.placeholder = 'Escribe un mensaje de apoyo...';
    } else {
        if (type === 'COMIDA') {
            if (labelCant) labelCant.textContent = 'Peso aproximado (Kg)';
            if (iconCant) iconCant.setAttribute('data-lucide', 'package');
            if (inputCant) {
                inputCant.placeholder = 'Ej: 15';
                inputCant.step = "0.5";
            }
            if (labelDesc) labelDesc.textContent = 'Marca y tipo de alimento';
            if (inputDesc) inputDesc.placeholder = 'Ej: Pienso para cachorros, latas húmedas...';
        } else if (type === 'MEDICINAS') {
            if (labelCant) labelCant.textContent = 'Unidades / Cajas';
            if (iconCant) iconCant.setAttribute('data-lucide', 'pill');
            if (inputCant) {
                inputCant.placeholder = 'Ej: 2';
                inputCant.step = "1";
            }
            if (labelDesc) labelDesc.textContent = 'Nombre y uso del medicamento';
            if (inputDesc) inputDesc.placeholder = 'Ej: Desparasitante, 3 cajas de Amoxicilina...';
        } else { // OTRO
            if (labelCant) labelCant.textContent = 'Cantidad (opcional)';
            if (iconCant) iconCant.setAttribute('data-lucide', 'gift');
            if (inputCant) {
                inputCant.placeholder = 'Ej: 1';
                inputCant.step = "1";
                inputCant.required = false;
            }
            if (labelDesc) labelDesc.textContent = 'Descripción del material';
            if (inputDesc) inputDesc.placeholder = 'Ej: Mantas, correas, juguetes...';
        }
    }
    if (window.lucide) lucide.createIcons();
}

// Establecer cantidad de donación
function setAmount(val, btn) {
    const input = document.getElementById('input-cantidad');
    if (input) input.value = val;
    updateButtons(btn);
}

function focusAmount(btn) {
    const input = document.getElementById('input-cantidad');
    if (input) input.focus();
    updateButtons(btn);
}

// Actualizar estado activo de los botones de cantidad
function updateButtons(activeBtn) {
    document.querySelectorAll('.amount-btn').forEach(b => b.classList.remove('active'));
    if (activeBtn) activeBtn.classList.add('active');
}

// Establecer recurrencia de la donación
function setRecurrence(type) {
    document.querySelectorAll('.view-toggle .toggle-btn').forEach(b => b.classList.remove('active'));
    const btn = document.getElementById(type === 'mensual' ? 'btn-mensual' : 'btn-unica');
    if (btn) btn.classList.add('active');
    const input = document.getElementById('frecuencia-input');
    if (input) input.value = type.toUpperCase();
    const text = document.getElementById('submit-text');
    if (text) text.textContent = type === 'mensual' ? 'Confirmar Donación Mensual' : 'Confirmar Donación';
}



