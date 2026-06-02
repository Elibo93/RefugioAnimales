// --- UI.JS ---

// Lógica de Modales (Sin cambios)
function closeModal() {
    const modalHost = document.getElementById('modal-host');
    if (modalHost) {
        modalHost.innerHTML = '';
        document.body.classList.remove('modal-open');
    }
}


// === GESTIÓN DE CONTRASEÑA (MODAL) ===
async function verifyPasswordGate(btn) {
    const userId = btn.getAttribute('data-user-id');

    // Recuperar Token CSRF
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');

    const { value: password } = await Swal.fire({
        title: window.refugioI18n?.securityTitle || 'Verificación de Seguridad',
        html: `
            <div style="text-align: center;">
                <div style="width: 70px; height: 70px; background: var(--primary-light); border-radius: 24px; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);">
                    <i data-lucide="shield-check" style="width: 32px; color: var(--primary);"></i>
                </div>
                <p style="color: var(--text-muted); font-size: 0.95rem; line-height: 1.5; margin-bottom: 25px;">
                    ${window.refugioI18n?.securityDesc || 'Por seguridad, confirma tu identidad introduciendo tu contraseña actual.'}
                </p>
                <div style="position: relative; max-width: 320px; margin: 0 auto; display: block;">
                    <input type="password" id="swal-input-password" class="swal2-input premium-modal-input" 
                           placeholder="${window.refugioI18n?.securityPlaceholder || 'Tu contraseña actual'}" 
                           style="width: 100%; margin: 0; padding-right: 50px; padding-left: 20px; border-radius: 16px; height: 55px; border: 2px solid #e2e8f0; transition: all 0.3s ease;">
                    <button type="button" onclick="togglePasswordVisibility('swal-input-password', this)" 
                            style="position: absolute; right: 15px; top: 50%; transform: translateY(-50%); background: #f8fafc; border: none; cursor: pointer; color: var(--text-muted); width: 35px; height: 35px; border-radius: 10px; display: flex; align-items: center; justify-content: center; z-index: 10; transition: all 0.2s;">
                        <i data-lucide="eye" style="width: 18px;"></i>
                    </button>
                </div>
            </div>
        `,
        showCancelButton: true,
        confirmButtonText: window.refugioI18n?.btnVerify || 'Verificar ahora',
        cancelButtonText: window.refugioI18n?.btnCancel || 'Cancelar',
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
            title: window.refugioI18n?.validating || 'Validando...',
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
                    title: window.refugioI18n?.deniedTitle || 'Acceso Denegado',
                    text: errorData.message || window.refugioI18n?.deniedDesc || 'La contraseña actual no es correcta.',
                    confirmButtonColor: '#ef4444',
                    customClass: { popup: 'premium-modal-radius', confirmButton: 'premium-modal-btn' }
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: window.refugioI18n?.errorTitle || 'Error de Conexión',
                text: window.refugioI18n?.errorDesc || 'No se pudo contactar con el servicio de seguridad.',
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
            btn.innerHTML = '<i data-lucide="check-circle" style="width: 20px;"></i> ' + (window.refugioI18n?.btnUpdate || 'Actualizar Contraseña');
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
        btn.innerHTML = '<i data-lucide="check-circle" style="width: 20px;"></i> ' + (window.refugioI18n?.btnUpdate || 'Actualizar Contraseña');
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
        errorDiv.innerText = window.refugioI18n?.errMismatch || "Las contraseñas no coinciden";
        errorDiv.style.display = 'block';
        return;
    }

    if (newPassword.length < 6) {
        errorDiv.innerText = window.refugioI18n?.errLength || "La contraseña debe tener al menos 6 caracteres";
        errorDiv.style.display = 'block';
        return;
    }

    // 2. UI FEEDBACK
    errorDiv.style.display = 'none';
    btn.disabled = true;
    btn.innerHTML = '<i class="animate-spin" data-lucide="loader-2"></i> ' + (window.refugioI18n?.saving || 'Guardando...');
    if (window.lucide) lucide.createIcons();

    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    if (csrfHeader && csrfToken) {
        headers[csrfHeader] = csrfToken;
    }

    try {
        const response = await fetch(`/web/personas/${userId}/cambiar-password`, {
            method: 'POST',
            headers: headers,
            body: `newPassword=${encodeURIComponent(newPassword)}`
        });

        if (response.ok) {
            closePasswordModal();
            Swal.fire({
                icon: 'success',
                title: window.refugioI18n?.successTitle || '¡Seguridad actualizada!',
                text: window.refugioI18n?.successDesc || 'Tu contraseña ha sido cambiada correctamente.',
                confirmButtonColor: '#4f46e5',
                customClass: { popup: 'premium-swal' }
            });
        } else {
            const data = await response.json().catch(() => ({ message: window.refugioI18n?.errServer || "Error en el servidor" }));
            errorDiv.innerText = data.message || window.refugioI18n?.errUpdate || "Error al actualizar la contraseña";
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        errorDiv.innerText = window.refugioI18n?.errConnection || "Error de conexión con el servidor";
        errorDiv.style.display = 'block';
    } finally {
        // Siempre restauramos el botón si no se ha cerrado el modal
        if (document.getElementById('passwordModal').style.display !== 'none') {
            btn.disabled = false;
            btn.innerHTML = '<i data-lucide="check-circle" style="width: 20px;"></i> ' + (window.refugioI18n?.btnUpdate || 'Actualizar Contraseña');
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
    // Buscar el contenedor de toasts o crear uno si no existe
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
            confirmButtonText: window.refugioI18n?.btnExcellent || '¡Excelente!',
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

    menu.style.display = 'flex';

    const animalNombre = nombre || 'el animal';
    const currentUrl = encodeURIComponent(window.location.href);
    const shareText = encodeURIComponent('¡Mira a ' + animalNombre + ' en adopción!');

    const waBtn = document.getElementById('share-wa');
    if (waBtn) waBtn.href = 'https://wa.me/?text=' + shareText + '%20' + currentUrl;

    const fbBtn = document.getElementById('share-fb');
    if (fbBtn) fbBtn.href = 'https://www.facebook.com/sharer/sharer.php?u=' + currentUrl;

    const xBtn = document.getElementById('share-x');
    if (xBtn) xBtn.href = 'https://twitter.com/intent/tweet?text=' + shareText + '&url=' + currentUrl;

    const mailBtn = document.getElementById('share-mail');
    if (mailBtn) mailBtn.href = 'mailto:?subject=¡Adopta a ' + animalNombre + '!&body=Mira su perfil aquí: ' + window.location.href;
}

// Cerrar el menú de compartición al hacer clic fuera
document.addEventListener('click', function(event) {
    const menu = document.getElementById('share-dropdown-menu');
    if (menu && menu.style.display === 'flex' && !event.target.closest('.share-container')) {
        menu.style.display = 'none';
    }
});
