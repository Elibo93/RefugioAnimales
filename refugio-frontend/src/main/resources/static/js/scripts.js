document.addEventListener('DOMContentLoaded', () => {
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

    // Inicialización global de componentes
    refreshDynamicComponents();
});

// === FUNCIONES GLOBALES ===

// Actualización general de componentes dinámicos
function refreshDynamicComponents() {
    if (window.lucide) {
        lucide.createIcons();
    }

    // 1. Lógica del Sidebar (Menú lateral)
    const openSidebarBtn = document.getElementById('open-sidebar');
    const closeSidebarBtn = document.getElementById('close-sidebar');
    const sidebar = document.getElementById('app-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');

    if (openSidebarBtn && sidebar && sidebarOverlay && !openSidebarBtn.dataset.listenerRegistered) {
        const toggleSidebar = () => {
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active');
            document.body.style.overflow = sidebar.classList.contains('active') ? 'hidden' : '';
        };

        openSidebarBtn.addEventListener('click', (e) => {
            e.preventDefault();
            toggleSidebar();
        });
        if (closeSidebarBtn) closeSidebarBtn.addEventListener('click', toggleSidebar);
        sidebarOverlay.addEventListener('click', toggleSidebar);
        openSidebarBtn.dataset.listenerRegistered = "true";
    }

    // 2. Lógica de Desplegables (Dropdowns)
    const dropdownTrigger = document.querySelector('.dropdown-trigger');
    const dropdownMenu = document.querySelector('.dropdown-menu');
    if (dropdownTrigger && dropdownMenu && !dropdownTrigger.dataset.listenerRegistered) {
        dropdownTrigger.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdownMenu.classList.toggle('active');
        });
        dropdownTrigger.dataset.listenerRegistered = "true";
    }

    // 3. Lógica de Submenús en el Sidebar
    document.querySelectorAll('.submenu-toggle').forEach(toggle => {
        if (!toggle.dataset.listenerRegistered) {
            toggle.addEventListener('click', () => {
                const group = toggle.closest('.sidebar-item-group');
                const submenu = group.querySelector('.sidebar-submenu');
                group.classList.toggle('open');
                if (submenu) submenu.classList.toggle('open');
            });
            toggle.dataset.listenerRegistered = "true";
        }
    });

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
}

// Lógica de Modales (Sin cambios)
function closeModal() {
    const modalHost = document.getElementById('modal-host');
    if (modalHost) {
        modalHost.innerHTML = '';
        document.body.classList.remove('modal-open');
    }
}

// Lógica de Carrusel (Sin cambios importantes)
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

// Lógica de Paginación OPTIMIZADA (No rompe el layout)
function initPagination(gridId, itemsPerPage = 8) {
    const runInit = () => {
        const grid = document.getElementById(gridId);
        if (!grid || grid.dataset.paginated === "true") return; // Evitar re-paginar si ya está OK
        
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
            return;
        } else if (controls) {
            controls.style.display = 'flex';
        }
        
        function showPage(page) {
            currentPage = page;
            const start = (page - 1) * itemsPerPage;
            const end = start + itemsPerPage;
            
            items.forEach((item, index) => {
                const isVisible = (index >= start && index < end);
                if (isVisible) {
                    item.style.removeProperty('display'); // Dejamos que el CSS mande (Flex/Grid)
                } else {
                    item.style.display = 'none';
                }
            });
            
            if (pageNumSpan) pageNumSpan.textContent = page;
            if (prevBtn) prevBtn.disabled = (page === 1);
            if (nextBtn) nextBtn.disabled = (page === totalPages);
        }
        
        if (prevBtn) prevBtn.onclick = () => { if (currentPage > 1) showPage(currentPage - 1); };
        if (nextBtn) nextBtn.onclick = () => { if (currentPage < totalPages) showPage(currentPage + 1); };
        
        showPage(1);
        grid.dataset.paginated = "true";
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
    // Buscamos tanto filas de tabla como tarjetas premium
    const elements = document.querySelectorAll('.task-row, .premium-row-card');
    elements.forEach(el => {
        const rowPriority = el.getAttribute('data-priority');
        if (priority === 'ALL' || rowPriority === priority) {
            // Si es una fila de tabla (TR), usamos table-row; si es una tarjeta (DIV), usamos flex
            el.style.display = (el.tagName === 'TR') ? 'table-row' : 'flex';
        } else {
            el.style.display = 'none';
        }
    });
}

// Lógica del Formulario de Donaciones
function handleTypeChange() {
    const typeSelect = document.getElementById('donation-type-select');
    if (!typeSelect) return;
    
    const type = typeSelect.value;
    const panelDineroExtra = document.getElementById('panel-dinero-extra');
    const panelObjetivo = document.getElementById('panel-objetivo');
    const panelUnificado = document.getElementById('panel-unificado');
    const actions = document.getElementById('form-actions');
    const labelCant = document.getElementById('label-cantidad');
    const iconCant = document.getElementById('icon-cantidad');
    const inputCant = document.getElementById('input-cantidad');
    const labelDesc = document.getElementById('label-descripcion');
    const inputDesc = document.getElementById('input-descripcion');

    // Resetear visibilidad base
    if (panelUnificado) panelUnificado.style.display = 'block';
    if (actions) actions.style.display = 'flex';

    if (type === 'DINERO') {
        if (panelDineroExtra) panelDineroExtra.style.setProperty('display', 'block', 'important');
        if (panelObjetivo) panelObjetivo.style.setProperty('display', 'block', 'important');
        if (labelCant) labelCant.textContent = 'Importe de la donación (€)';
        if (iconCant) iconCant.setAttribute('data-lucide', 'euro');
        if (inputCant) {
            inputCant.placeholder = '0.00';
            inputCant.type = 'number';
            inputCant.step = '0.01';
        }
        if (labelDesc) labelDesc.textContent = 'Mensaje o dedicatoria (opcional)';
    } else {
        if (panelDineroExtra) panelDineroExtra.style.setProperty('display', 'none', 'important');
        if (panelObjetivo) panelObjetivo.style.setProperty('display', 'none', 'important');
        
        if (type === 'COMIDA') {
            if (labelCant) labelCant.textContent = 'Peso aproximado (Kg)';
            if (iconCant) iconCant.setAttribute('data-lucide', 'package');
            if (inputCant) {
                inputCant.placeholder = 'Ej: 15';
                inputCant.type = 'number';
                inputCant.step = '0.5';
            }
            if (labelDesc) labelDesc.textContent = 'Marca y tipo de alimento';
        } else if (type === 'MEDICINAS' || type === 'MEDICAMENTO') {
            if (labelCant) labelCant.textContent = 'Unidades / Cajas';
            if (iconCant) iconCant.setAttribute('data-lucide', 'pill');
            if (inputCant) {
                inputCant.placeholder = 'Ej: 2';
                inputCant.type = 'number';
                inputCant.step = '1';
            }
            if (labelDesc) labelDesc.textContent = 'Nombre y uso del medicamento';
        } else {
            if (labelCant) labelCant.textContent = 'Cantidad / Unidades';
            if (iconCant) iconCant.setAttribute('data-lucide', 'gift');
            if (inputCant) {
                inputCant.placeholder = 'Ej: 5';
                inputCant.type = 'text';
            }
            if (labelDesc) labelDesc.textContent = 'Descripción del material';
        }
    }
    if (window.lucide) lucide.createIcons();
}

// Establecer cantidad de donación
function setAmount(val, btn) {
    const input = document.getElementById('input-cantidad');
    if (input) input.value = val;
    
    document.querySelectorAll('.amount-btn').forEach(b => {
        b.style.borderColor = '#e2e8f0';
        b.style.background = 'white';
        b.style.color = 'inherit';
    });
    
    if (btn) {
        btn.style.borderColor = '#166534';
        btn.style.background = '#f0fdf4';
        btn.style.color = '#166534';
    }
}

function focusAmount(btn) {
    const input = document.getElementById('input-cantidad');
    if (input) input.focus();
    setAmount('', null); // Limpiar selección previa
    if (btn) {
        btn.style.borderColor = '#166534';
    }
}

// Establecer recurrencia de la donación
function setRecurrence(type) {
    const freqInput = document.getElementById('frecuencia-input');
    if (freqInput) freqInput.value = type.toUpperCase();
    
    const btnUnica = document.getElementById('btn-unica');
    const btnMensual = document.getElementById('btn-mensual');
    
    if (btnUnica && btnMensual) {
        if (type === 'unica') {
            btnUnica.style.background = 'white';
            btnUnica.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
            btnMensual.style.background = 'transparent';
            btnMensual.style.boxShadow = 'none';
        } else {
            btnMensual.style.background = 'white';
            btnMensual.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
            btnUnica.style.background = 'transparent';
            btnUnica.style.boxShadow = 'none';
        }
    }

    const text = document.getElementById('submit-text');
    if (text) text.textContent = type === 'mensual' ? 'Confirmar Donación Mensual' : 'Confirmar Donación';
}

// Lógica de Selección de Usuario (Autocomplete)
function selectUser(id, fullName, isRegistered, adoptanteId) {
    // Normalizar adoptanteId: si llega como string 'null', undefined o vacío, lo tratamos como nulo real
    const effectiveAId = (adoptanteId && adoptanteId !== 'null' && adoptanteId !== 'undefined' && adoptanteId !== '') ? adoptanteId : null;

    const searchInput = document.getElementById('user-search');
    const idInput = document.getElementById('usuarioId-input');
    const suggestions = document.getElementById('user-suggestions');

    // CASO 1: Estamos en contexto de solicitud y tenemos un adoptanteId real
    if (effectiveAId) {
        if (searchInput) searchInput.value = fullName;
        if (idInput) idInput.value = effectiveAId;
        if (suggestions) suggestions.innerHTML = '';
        return;
    }

    // CASO 2: Estamos registrando un nuevo rol y el usuario ya lo tiene
    if (isRegistered) {
        const errorMsg = "Este usuario ya cuenta con un perfil registrado para este rol.";
        if (typeof showToast === 'function') {
            showToast(errorMsg, 'error');
        } else {
            alert(errorMsg);
        }
        return;
    }

    // CASO 3: Registro normal (Voluntario / Nuevo Adoptante) usando el ID de usuario
    if (searchInput) searchInput.value = fullName;
    if (idInput) idInput.value = id;
    if (suggestions) suggestions.innerHTML = '';
}

// === GESTIÓN DE CONTRASEÑA (MODAL) ===
async function verifyPasswordGate(btn) {
    const userId = btn.getAttribute('data-user-id');
    
    // Recuperar Token CSRF
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    
    const { value: password } = await Swal.fire({
        title: 'Verificación de Seguridad',
        html: `
            <div style="text-align: center;">
                <div style="width: 70px; height: 70px; background: var(--primary-light); border-radius: 24px; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);">
                    <i data-lucide="shield-check" style="width: 32px; color: var(--primary);"></i>
                </div>
                <p style="color: var(--text-muted); font-size: 0.95rem; line-height: 1.5; margin-bottom: 25px;">
                    Por seguridad, confirma tu identidad introduciendo tu contraseña actual.
                </p>
                <div style="position: relative; max-width: 320px; margin: 0 auto; display: block;">
                    <input type="password" id="swal-input-password" class="swal2-input premium-modal-input" 
                           placeholder="Tu contraseña actual" 
                           style="width: 100%; margin: 0; padding-right: 50px; padding-left: 20px; border-radius: 16px; height: 55px; border: 2px solid #e2e8f0; transition: all 0.3s ease;">
                    <button type="button" onclick="togglePasswordVisibility('swal-input-password', this)" 
                            style="position: absolute; right: 15px; top: 50%; transform: translateY(-50%); background: #f8fafc; border: none; cursor: pointer; color: var(--text-muted); width: 35px; height: 35px; border-radius: 10px; display: flex; align-items: center; justify-content: center; z-index: 10; transition: all 0.2s;">
                        <i data-lucide="eye" style="width: 18px;"></i>
                    </button>
                </div>
            </div>
        `,
        showCancelButton: true,
        confirmButtonText: 'Verificar ahora',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: 'var(--primary)',
        cancelButtonColor: '#94a3b8',
        padding: '2.5rem',
        background: '#ffffff',
        backdrop: 'rgba(15, 23, 42, 0.4) blur(10px)',
        didOpen: () => {
            if (window.lucide) {
                lucide.createIcons({
                    root: Swal.getHtmlContainer()
                });
            }
            document.getElementById('swal-input-password').focus();
        },
        preConfirm: () => {
            const val = document.getElementById('swal-input-password').value;
            if (!val) {
                Swal.showValidationMessage('La contraseña es obligatoria');
            }
            return val;
        },
        customClass: {
            popup: 'premium-modal-radius',
            title: 'premium-modal-title',
            confirmButton: 'premium-modal-btn',
            cancelButton: 'premium-modal-btn-outline'
        }
    });

    if (password) {
        Swal.fire({
            title: 'Validando...',
            allowOutsideClick: false,
            didOpen: () => { Swal.showLoading(); },
            customClass: { popup: 'premium-modal-radius' }
        });

        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };
            if (csrfHeader && csrfToken) {
                headers[csrfHeader] = csrfToken;
            }

            // Enviamos como Form Data para máxima compatibilidad con el controlador actual
            const response = await fetch(`/web/personas/${userId}/verificar-password`, {
                method: 'POST',
                headers: headers,
                body: `password=${encodeURIComponent(password)}`
            });

            if (response.ok) {
                Swal.close();
                openPasswordModal();
            } else {
                const errorData = await response.json().catch(() => ({}));
                console.error('[DEBUG] Error en verificación:', response.status, errorData);
                
                Swal.fire({
                    icon: 'error',
                    title: 'Acceso Denegado',
                    text: errorData.message || 'La contraseña actual no es correcta.',
                    confirmButtonColor: '#ef4444',
                    customClass: { popup: 'premium-modal-radius', confirmButton: 'premium-modal-btn' }
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error de Conexión',
                text: 'No se pudo contactar con el servicio de seguridad.',
                confirmButtonColor: '#ef4444',
                customClass: { popup: 'premium-modal-radius' }
            });
        }
    }
}

function openPasswordModal() {
    const modal = document.getElementById('passwordModal');
    const btn = document.querySelector('#passwordForm button[type="submit"]');
    if (modal) {
        modal.style.display = 'flex';
        // Resetear botón por seguridad al abrir
        if (btn) {
            btn.innerHTML = '<i data-lucide="check-circle" style="width: 20px;"></i> Actualizar Contraseña';
            btn.disabled = false;
        }
        const input = document.getElementById('newPassword');
        if (input) input.focus();
        if (window.lucide) lucide.createIcons();
    }
}

function closePasswordModal() {
    const modal = document.getElementById('passwordModal');
    const form = document.getElementById('passwordForm');
    const errorDiv = document.getElementById('passwordError');
    const btn = document.querySelector('#passwordForm button[type="submit"]');
    
    if (modal) modal.style.display = 'none';
    if (form) form.reset();
    if (errorDiv) errorDiv.style.display = 'none';
    if (btn) {
        btn.innerHTML = '<i data-lucide="check-circle" style="width: 20px;"></i> Actualizar Contraseña';
        btn.disabled = false;
    }
    if (window.lucide) lucide.createIcons();
}

async function handlePasswordUpdatePremium(event) {
    event.preventDefault();
    const form = event.target;
    const btn = form.querySelector('button[type="submit"]');
    
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const errorDiv = document.getElementById('passwordError');
    
    const userId = form.getAttribute('data-user-id');

    // 1. VALIDACIONES LOCALES
    if (newPassword !== confirmPassword) {
        errorDiv.innerText = "Las contraseñas no coinciden";
        errorDiv.style.display = 'block';
        return;
    }

    if (newPassword.length < 6) {
        errorDiv.innerText = "La contraseña debe tener al menos 6 caracteres";
        errorDiv.style.display = 'block';
        return;
    }

    // 2. UI FEEDBACK
    errorDiv.style.display = 'none';
    btn.disabled = true;
    btn.innerHTML = '<i class="animate-spin" data-lucide="loader-2"></i> Guardando...';
    if (window.lucide) lucide.createIcons();

    try {
        const response = await fetch(`/web/personas/${userId}/cambiar-password`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `newPassword=${encodeURIComponent(newPassword)}`
        });

        if (response.ok) {
            Swal.fire({
                icon: 'success',
                title: '¡Seguridad actualizada!',
                text: 'Tu contraseña ha sido cambiada correctamente.',
                confirmButtonColor: '#4f46e5',
                customClass: { popup: 'premium-swal' }
            }).then(() => {
                closePasswordModal();
            });
        } else {
            const data = await response.json().catch(() => ({ message: "Error en el servidor" }));
            errorDiv.innerText = data.message || "Error al actualizar la contraseña";
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        errorDiv.innerText = "Error de conexión con el servidor";
        errorDiv.style.display = 'block';
    } finally {
        // Siempre restauramos el botón si no se ha cerrado el modal
        if (document.getElementById('passwordModal').style.display !== 'none') {
            btn.disabled = false;
            btn.innerHTML = '<i data-lucide="check-circle" style="width: 20px;"></i> Actualizar Contraseña';
            if (window.lucide) lucide.createIcons();
        }
    }
}

function togglePasswordVisibility(inputId, btn) {
    const input = document.getElementById(inputId);
    // Buscamos el icono (puede ser un <i> o un <svg> si Lucide ya lo reemplazó)
    const icon = btn.querySelector('i, svg');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.setAttribute('data-lucide', 'eye-off');
    } else {
        input.type = 'password';
        icon.setAttribute('data-lucide', 'eye');
    }
    
    // Forzamos a Lucide a re-escanear el DOM para actualizar el icono
    if (window.lucide) {
        lucide.createIcons();
    }
}
