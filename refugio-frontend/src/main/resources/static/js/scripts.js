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

/**
 * Muestra una notificación toast programáticamente
 * @param {string} message Mensaje a mostrar
 * @param {string} type Tipo de notificación ('success' o 'error')
 */
function showToast(message, type = 'success') {
    // Buscar el contenedor de toasts o crear uno si no existe (podría estar en main-layout)
    // Pero como ya tenemos fragmentos, simplemente inyectamos en el body o en un host
    const toastHost = document.createElement('div');
    toastHost.className = `toast ${type}`;
    toastHost.style.position = 'fixed';
    toastHost.style.top = '20px';
    toastHost.style.right = '20px';
    toastHost.style.zIndex = '9999';
    toastHost.style.opacity = '0';
    toastHost.style.transform = 'translateY(-20px)';
    toastHost.style.transition = 'all 0.5s ease';
    
    const iconName = type === 'success' ? 'check-circle' : 'alert-circle';
    toastHost.innerHTML = `<i data-lucide="${iconName}" style="width:20px; margin-right: 10px;"></i> <span>${message}</span>`;
    
    document.body.appendChild(toastHost);
    
    // Forzar reflow para animación de entrada
    void toastHost.offsetWidth;
    
    toastHost.style.opacity = '1';
    toastHost.style.transform = 'translateY(0)';
    
    if (window.lucide) lucide.createIcons({ root: toastHost });
    
    // Auto-ocultar
    setTimeout(() => {
        toastHost.style.opacity = '0';
        toastHost.style.transform = 'translateY(-20px)';
        setTimeout(() => toastHost.remove(), 500);
    }, 5000);
}

// 7. Escucha de eventos HTMX para disparar Toasts desde el servidor
document.body.addEventListener('showToast', (evt) => {
    if (evt.detail) {
        showToast(evt.detail.message, evt.detail.type || 'success');
    }
});

// 8. Escucha de eventos HTMX para actualizar contadores de voluntarios
document.body.addEventListener('volunteerStatusChanged', () => {
    // 1. Actualizar contador en Sidebar
    const vBadge = document.getElementById('sidebar-voluntarios-badge');
    if (vBadge) {
        let count = parseInt(vBadge.textContent) || 0;
        count = Math.max(0, count - 1);
        if (count > 0) {
            vBadge.textContent = count;
        } else {
            vBadge.remove();
        }
        
        // Actualizar punto de Revisiones
        const aBadge = document.getElementById('sidebar-adopciones-badge');
        const revDot = document.getElementById('sidebar-revisiones-dot');
        const aCount = aBadge ? (parseInt(aBadge.textContent) || 0) : 0;
        if (count + aCount <= 0 && revDot) {
            revDot.remove();
        }
    }

    // 2. Si estamos en la página de voluntarios pendientes, recargar contenido dinámicamente
    if (window.location.pathname === '/web/voluntarios/pendientes') {
        if (typeof htmx !== 'undefined') {
            htmx.ajax('GET', '/web/voluntarios/pendientes', { target: '#contenido-dinamico', swap: 'innerHTML' });
        } else {
            window.location.reload();
        }
    }
});

// 9. Escucha de eventos HTMX para actualizar contadores de adopciones
document.body.addEventListener('adoptionStatusChanged', () => {
    // 1. Actualizar contador en Sidebar
    const aBadge = document.getElementById('sidebar-adopciones-badge');
    if (aBadge) {
        let count = parseInt(aBadge.textContent) || 0;
        count = Math.max(0, count - 1);
        if (count > 0) {
            aBadge.textContent = count;
        } else {
            aBadge.remove();
        }
        
        // Actualizar punto de Revisiones
        const vBadge = document.getElementById('sidebar-voluntarios-badge');
        const revDot = document.getElementById('sidebar-revisiones-dot');
        const vCount = vBadge ? (parseInt(vBadge.textContent) || 0) : 0;
        if (count + vCount <= 0 && revDot) {
            revDot.remove();
        }
    }
});
/**
 * Dispara una celebración visual con confeti y un modal premium
 * @param {string} title Título del logro
 * @param {string} message Mensaje descriptivo
 */
function celebrateAchievement(title, message, imageUrl = null) {
    if (typeof confetti !== 'undefined') {
        const count = 200;
        const defaults = {
            origin: { y: 0.7 },
            zIndex: 10000
        };

        function fire(particleRatio, opts) {
            confetti({
                ...defaults,
                ...opts,
                particleCount: Math.floor(count * particleRatio)
            });
        }

        fire(0.25, { spread: 26, startVelocity: 55 });
        fire(0.2, { spread: 60 });
        fire(0.35, { spread: 100, decay: 0.91, scalar: 0.8 });
        fire(0.1, { spread: 120, startVelocity: 25, decay: 0.92, scalar: 1.2 });
        fire(0.1, { spread: 120, startVelocity: 45 });
    }

    if (window.Swal) {
        let iconHtml = `<div style="color: #fbbf24; margin-bottom: 10px; font-size: 3rem;">🏆</div>`;
        if (imageUrl) {
            iconHtml = `<img src="${imageUrl}" style="width: 100px; height: 100px; object-fit: contain; margin: 0 auto 15px; display: block; filter: drop-shadow(0px 10px 15px rgba(0,0,0,0.15)); animation: float 3s ease-in-out infinite;">`;
        }

        Swal.fire({
            title: `${iconHtml} ${title}`,
            text: message,
            confirmButtonText: '¡Excelente!',
            confirmButtonColor: '#15803d',
            background: '#ffffff',
            padding: '2.5rem',
            backdrop: 'rgba(15, 23, 42, 0.4) blur(8px)',
            customClass: {
                popup: 'premium-modal-radius achievement-popup',
                title: 'premium-modal-title',
                confirmButton: 'premium-modal-btn'
            }
        });
    }
}

/**
 * Alterna la visibilidad del menú de compartición premium y configura sus enlaces dinámicamente.
 * @param {Event} event Evento del click
 * @param {string} nombre Nombre del animal
 */
function toggleShareMenu(event, nombre) {
    event.stopPropagation(); // Evitar que se propague y cierre el menú de inmediato
    
    const menu = document.getElementById('share-dropdown-menu');
    if (!menu) return;
    
    // Si ya está abierto, lo cerramos
    if (menu.style.display === 'flex') {
        menu.style.display = 'none';
        return;
    }
    
    const animalNombre = nombre || 'el animal';
    const currentUrl = window.location.href;
    const shareText = 'Mira el perfil de ' + animalNombre + ' en el Refugio de Animales. ¡Ayúdanos a encontrarle un hogar!';
    
    // Configurar enlaces de redes sociales
    const waBtn = document.getElementById('share-wa');
    const fbBtn = document.getElementById('share-fb');
    const xBtn = document.getElementById('share-x');
    const mailBtn = document.getElementById('share-mail');
    
    if (waBtn) {
        waBtn.href = 'https://api.whatsapp.com/send?text=' + encodeURIComponent(shareText + ' ' + currentUrl);
    }
    if (fbBtn) {
        fbBtn.href = 'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(currentUrl);
    }
    if (xBtn) {
        xBtn.href = 'https://twitter.com/intent/tweet?text=' + encodeURIComponent(shareText) + '&url=' + encodeURIComponent(currentUrl);
    }
    if (mailBtn) {
        mailBtn.href = 'mailto:?subject=' + encodeURIComponent('Refugio de Animales: Conoce a ' + animalNombre) + '&body=' + encodeURIComponent(shareText + '\n\nEnlace: ' + currentUrl);
    }
    
    // Mostrar el menú
    menu.style.display = 'flex';
    
    // Registrar listener único para cerrar al hacer click fuera
    const closeListener = (e) => {
        if (!menu.contains(e.target) && e.target !== event.currentTarget) {
            menu.style.display = 'none';
            document.removeEventListener('click', closeListener);
        }
    };
    
    // Pequeño retardo para no pillar el propio click actual
    setTimeout(() => {
        document.addEventListener('click', closeListener);
    }, 50);
}

/**
 * Muestra un selector premium de mapas para elegir la aplicación de navegación
 * @param {Event} event Evento del click original
 */
function openMapsSelector(event) {
    if (event) event.preventDefault();
    
    const address = "Calle del Refugio, 12, Madrid, España";
    const encodedAddress = encodeURIComponent(address);
    
    const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${encodedAddress}`;
    const wazeUrl = `https://waze.com/ul?q=${encodedAddress}`;
    const appleMapsUrl = `maps://maps.apple.com/?q=${encodedAddress}`;
    const webAppleMapsUrl = `https://maps.apple.com/?q=${encodedAddress}`;
    
    // Comprobar si es un dispositivo Apple para usar el esquema nativo o la web
    const isAppleDevice = /iPad|iPhone|iPod|Macintosh/.test(navigator.userAgent) && !window.MSStream;
    const finalAppleUrl = isAppleDevice ? appleMapsUrl : webAppleMapsUrl;

    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: '¿Cómo deseas llegar?',
            html: `
                <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 24px; text-align: center;">
                    Selecciona tu aplicación favorita para ver la ubicación del refugio.
                </p>
                <div style="display: flex; flex-direction: column; gap: 12px; max-width: 320px; margin: 0 auto;">
                    <a href="${googleMapsUrl}" target="_blank" rel="noopener noreferrer" class="map-provider-btn google" style="display: flex; align-items: center; gap: 16px; padding: 14px 20px; border-radius: 16px; text-decoration: none; border: 1.5px solid #e2e8f0; background: white; transition: all 0.3s ease;">
                        <img src="/images/google-maps.png" style="width: 24px; height: 24px; object-fit: contain;" alt="Google Maps">
                        <span style="font-weight: 700; color: #334155; font-size: 0.95rem;">Google Maps</span>
                    </a>
                    <a href="${wazeUrl}" target="_blank" rel="noopener noreferrer" class="map-provider-btn waze" style="display: flex; align-items: center; gap: 16px; padding: 14px 20px; border-radius: 16px; text-decoration: none; border: 1.5px solid #e2e8f0; background: white; transition: all 0.3s ease;">
                        <img src="/images/waze-logo.png" style="width: 24px; height: 24px; object-fit: contain;" alt="Waze">
                        <span style="font-weight: 700; color: #334155; font-size: 0.95rem;">Waze</span>
                    </a>
                    <a href="${finalAppleUrl}" target="_blank" rel="noopener noreferrer" class="map-provider-btn apple" style="display: flex; align-items: center; gap: 16px; padding: 14px 20px; border-radius: 16px; text-decoration: none; border: 1.5px solid #e2e8f0; background: white; transition: all 0.3s ease;">
                        <img src="/images/apple-map.png" style="width: 24px; height: 24px; object-fit: contain;" alt="Apple Maps">
                        <span style="font-weight: 700; color: #334155; font-size: 0.95rem;">Apple Maps</span>
                    </a>
                </div>
                <style>
                    .map-provider-btn {
                        box-shadow: 0 2px 4px rgba(0,0,0,0.02);
                    }
                    .map-provider-btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(0,0,0,0.08) !important;
                        border-color: var(--primary) !important;
                        background: #f8fafc !important;
                    }
                    .map-provider-btn:hover span {
                        color: var(--primary) !important;
                    }
                </style>
            `,
            showConfirmButton: false,
            showCancelButton: true,
            cancelButtonText: 'Cancelar',
            cancelButtonColor: '#64748b',
            padding: '2rem',
            background: '#ffffff',
            backdrop: 'rgba(15, 23, 42, 0.4) blur(10px)',
            customClass: {
                popup: 'premium-modal-radius',
                cancelButton: 'btn btn-outline'
            }
        });
    } else {
        // Fallback en caso de que SweetAlert2 no esté cargado
        window.open(googleMapsUrl, '_blank');
    }
}

