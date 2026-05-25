// --- FORMS.JS ---

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
        if (labelCant) labelCant.textContent = window.refugioI18n?.donacionImporte || 'Importe de la donación (€)';
        if (iconCant) iconCant.setAttribute('data-lucide', 'euro');
        if (inputCant) {
            inputCant.placeholder = '0.00';
            inputCant.type = 'number';
            inputCant.step = '0.01';
        }
        if (labelDesc) labelDesc.textContent = window.refugioI18n?.donacionMensaje || 'Mensaje o dedicatoria (opcional)';
    } else {
        if (panelDineroExtra) panelDineroExtra.style.setProperty('display', 'none', 'important');
        if (panelObjetivo) panelObjetivo.style.setProperty('display', 'none', 'important');
        
        if (type === 'COMIDA') {
            if (labelCant) labelCant.textContent = window.refugioI18n?.donacionPeso || 'Peso aproximado (Kg)';
            if (iconCant) iconCant.setAttribute('data-lucide', 'package');
            if (inputCant) {
                inputCant.placeholder = window.refugioI18n?.donacionPlaceholder15 || 'Ej: 15';
                inputCant.type = 'number';
                inputCant.step = '0.5';
            }
            if (labelDesc) labelDesc.textContent = window.refugioI18n?.donacionMarca || 'Marca y tipo de alimento';
        } else if (type === 'MEDICINAS' || type === 'MEDICAMENTO') {
            if (labelCant) labelCant.textContent = window.refugioI18n?.donacionUnidades || 'Unidades / Cajas';
            if (iconCant) iconCant.setAttribute('data-lucide', 'pill');
            if (inputCant) {
                inputCant.placeholder = window.refugioI18n?.donacionPlaceholder2 || 'Ej: 2';
                inputCant.type = 'number';
                inputCant.step = '1';
            }
            if (labelDesc) labelDesc.textContent = window.refugioI18n?.donacionMedicamento || 'Nombre y uso del medicamento';
        } else {
            if (labelCant) labelCant.textContent = window.refugioI18n?.donacionCantidad || 'Cantidad / Unidades';
            if (iconCant) iconCant.setAttribute('data-lucide', 'gift');
            if (inputCant) {
                inputCant.placeholder = window.refugioI18n?.donacionPlaceholder5 || 'Ej: 5';
                inputCant.type = 'text';
            }
            if (labelDesc) labelDesc.textContent = window.refugioI18n?.donacionMaterial || 'Descripción del material';
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
    if (text) text.textContent = type === 'mensual' ? (window.refugioI18n?.donacionConfirmarMensual || 'Confirmar Donación Mensual') : (window.refugioI18n?.donacionConfirmar || 'Confirmar Donación');
}


// Lógica de Selección de Usuario (Autocomplete)
function selectUser(id, fullName, isRegistered, adoptanteId) {
    // Normalizar adoptanteId: si llega como string 'null', undefined o vacío, lo tratamos como nulo real
    const effectiveAId = (adoptanteId && adoptanteId !== 'null' && adoptanteId !== 'undefined' && adoptanteId !== '') ? adoptanteId : null;

    const searchInput = document.getElementById('user-search');
    const idInput = document.getElementById('usuarioId-input');
    const suggestions = document.getElementById('user-suggestions');

    // Manejo de la tarjeta de usuario seleccionado (Ej: Adopciones)
    const card = document.getElementById('selected-user-card');
    const nameTxt = document.getElementById('selected-user-name');

    // CASO 1: Estamos registrando un nuevo rol y el usuario ya lo tiene
    if (isRegistered) {
        const errorMsg = "Este usuario ya cuenta con un perfil registrado para este rol.";
        if (typeof showToast === 'function') {
            showToast(errorMsg, 'error');
        } else {
            alert(errorMsg);
        }
        return;
    }

    // CASO 2: Registro dinámico (Voluntario / Nuevo Adoptante)
    // Si estamos en la página de creación, en lugar de intentar rellenar inputs con JS, 
    // recargamos la página pasando el parámetro usuarioId para que el backend prepare el formulario dinámicamente.
    const path = window.location.pathname;
    if (path.includes('/voluntarios/nuevo') || path.includes('/adoptantes/nuevo')) {
        window.location.href = path + '?usuarioId=' + id;
        return;
    }

    // CASO 3: Estamos en contexto de solicitud y tenemos un adoptanteId real
    if (effectiveAId) {
        if (searchInput) searchInput.value = fullName;
        if (idInput) idInput.value = effectiveAId;
        if (suggestions) suggestions.innerHTML = '';
        
        if (card && nameTxt) {
            nameTxt.innerText = fullName;
            card.style.display = 'flex';
            if (window.lucide) lucide.createIcons();
        }
        return;
    }

    // Para otros contextos que puedan seguir usando la lógica anterior (Ej: si se usa en otra vista)
    if (searchInput) searchInput.value = fullName;
    if (idInput) idInput.value = id;
    
    // Si estamos en el formulario de Adoptante o similar donde hay inputs separados para nombre y apellido
    const nombreInput = document.getElementById('nombre-input');
    const apellidoInput = document.getElementById('apellido-input');
    if (nombreInput && apellidoInput && fullName) {
        const parts = fullName.split(' ');
        nombreInput.value = parts[0] || '';
        apellidoInput.value = parts.slice(1).join(' ') || '';
    }
    
    if (suggestions) suggestions.innerHTML = '';
    
    // Si hay tarjeta de voluntario o similar
    if (card && nameTxt) {
        nameTxt.innerText = fullName;
        card.style.display = 'flex';
        if (window.lucide) lucide.createIcons();
    }
}

function clearUserSelection() {
    const searchInput = document.getElementById('user-search');
    const idInput = document.getElementById('usuarioId-input');
    const card = document.getElementById('selected-user-card');

    if (idInput) idInput.value = '';
    if (searchInput) {
        searchInput.value = '';
        searchInput.focus();
    }
    if (card) card.style.display = 'none';
}

function openShareMenu(event, animalNombre, currentUrl, shareText) {
    const menu = document.getElementById('share-menu');
    if (!menu) return;

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
            title: window.refugioI18n?.mapTitle || '¿Cómo deseas llegar?',
            html: `
                <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 24px; text-align: center;">
                    ${window.refugioI18n?.mapDesc || 'Selecciona tu aplicación favorita para ver la ubicación del refugio.'}
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
            cancelButtonText: window.refugioI18n?.btnCancel || 'Cancelar',
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

// === TAREAS: LÓGICA DEL CALENDARIO ===
var viewMode = localStorage.getItem('tareasViewMode') || 'list';

function toggleViewMode() {
    viewMode = viewMode === 'list' ? 'calendar' : 'list';
    localStorage.setItem('tareasViewMode', viewMode);
    applyViewMode();
}

function applyViewMode() {
    const listContainers = document.querySelectorAll('.premium-list-container');
    const emptyMessage = document.querySelector('.content-body > div[style*="border: 1px dashed"]');
    const calView = document.getElementById('calendar-view');
    const icon = document.getElementById('view-icon');
    const pagination = document.querySelector('.pagination-container');
    const filterForm = document.getElementById('tareas-filter-form');
    
    if (viewMode === 'calendar') {
        listContainers.forEach(el => el.style.display = 'none');
        if (emptyMessage) {
            emptyMessage.setAttribute('data-hidden', 'true');
            emptyMessage.style.display = 'none';
        }
        if (pagination) pagination.style.display = 'none';
        // Mantener visibles los filtros en el calendario
        // if (filterForm) filterForm.style.display = 'none';
        
        if (calView) calView.style.display = 'block';
        if (icon) icon.setAttribute('data-lucide', 'list');
        if (window.lucide) lucide.createIcons();
        
        initCalendar();
    } else {
        if (calView) calView.style.display = 'none';
        listContainers.forEach(el => el.style.display = 'block');
        if (emptyMessage && emptyMessage.getAttribute('data-hidden') === 'true') {
            emptyMessage.style.display = 'flex';
            emptyMessage.removeAttribute('data-hidden');
        }
        if (pagination) pagination.style.display = 'flex';
        if (filterForm) filterForm.style.display = 'flex';
        
        if (icon) icon.setAttribute('data-lucide', 'calendar');
        if (window.lucide) lucide.createIcons();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('calendar-view')) {
        applyViewMode();
    }
});

function initCalendar() {
    var calendarEl = document.getElementById('calendar');
    if (!calendarEl) return;
    
    if (window.currentCalendar) {
        try { window.currentCalendar.destroy(); } catch(e) {}
    }
    
    var tareasObj = [];
    var dataTag = document.getElementById('calendar-data');
    if (dataTag && dataTag.textContent) {
        try {
            var rawText = dataTag.textContent.trim();
            // Evitar problemas si Thymeleaf deja comentarios multilínea residuales
            if (rawText.startsWith('/*')) {
                rawText = rawText.replace(/^[\s\S]*?\*\/[\s]*/, '');
            }
            tareasObj = JSON.parse(rawText);
        } catch(e) {
            console.error("Error al cargar datos del calendario:", e);
        }
    } else {
        tareasObj = window.currentTareas || []; // Fallback por si acaso


    }
    
    var eventsData = [];
    
    if (tareasObj && tareasObj.length > 0) {
        tareasObj.forEach(function(t) {
            if (!t.fechaLimite) return;
            
            var color = '#3b82f6';
            if (t.prioridad === 'ALTA') color = '#ef4444';
            else if (t.prioridad === 'MEDIA') color = '#f59e0b';
            else if (t.prioridad === 'BAJA') color = '#10b981';
            
            eventsData.push({
                id: t.id,
                title: t.descripcion,
                start: t.fechaLimite,
                backgroundColor: color,
                borderColor: color,
                url: '/web/tareas/' + t.id + '/historial',
                extendedProps: {
                    estado: t.estado
                }
            });
        });
    }

    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        displayEventTime: true,
        eventTimeFormat: { hour: '2-digit', minute: '2-digit', hour12: false },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,listWeek'
        },
        buttonText: {
            today: 'Hoy',
            month: 'Mes',
            week: 'Semana',
            list: 'Agenda'
        },
        locale: 'es',
        allDayText: 'Todo el día',
        firstDay: 1,
        events: eventsData,
        eventClick: function(info) {
            if (info.event.url) {
                info.jsEvent.preventDefault();
                window.location.href = info.event.url;
            }
        }
    });
    
    calendar.render();
    window.currentCalendar = calendar;
}

if (!window.htmxCalendarListenerAdded) {
    document.body.addEventListener('htmx:afterSettle', function(event) {
        if (document.getElementById('calendar-view')) {
            applyViewMode();
        }
    });
    window.htmxCalendarListenerAdded = true;
}

// === PERFIL (PERSONA-DETALLE): LÓGICA DEL CALENDARIO DE DISPONIBILIDAD ===
document.addEventListener('DOMContentLoaded', function() {
    var dispEl = document.getElementById('disp-calendar');
    if(!dispEl) return;
    
    // Establecer fecha mínima como hoy para no permitir elegir días pasados
    const hoy = new Date().toISOString().split('T')[0];
    const inputFecha = document.getElementById('input-fecha');
    if (inputFecha) {
        inputFecha.setAttribute('min', hoy);
    }

    const isOwner = window.isOwner || false;
    const disponibilidades = window.disponibilidades || [];
    
    const eventsData = disponibilidades.map(d => {
        let color = '#f59e0b';
        if (d.estado === 'DISPONIBLE') color = '#10b981';
        if (d.estado === 'NO_DISPONIBLE') color = '#ef4444';
        
        let tituloFormateado = d.turno;
        if (d.estado === 'NO_DISPONIBLE') {
            tituloFormateado = 'No Disponible';
        } else {
            if (d.turno === 'MANANA') tituloFormateado = 'Mañana';
            else if (d.turno === 'TARDE') tituloFormateado = 'Tarde';
            else if (d.turno === 'NOCHE') tituloFormateado = 'Noche';
            else if (d.turno === 'TODO_EL_DIA') tituloFormateado = 'Todo el día';
        }

        return {
            id: d.id,
            title: tituloFormateado,
            start: d.fecha,
            backgroundColor: color,
            borderColor: color,
            allDay: true,
            extendedProps: {
                estado: d.estado
            }
        };
    });

    const calendar = new FullCalendar.Calendar(dispEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,listWeek'
        },
        buttonText: {
            today: 'Hoy',
            month: 'Mes',
            list: 'Lista'
        },
        locale: 'es',
        allDayText: 'Todo el día',


        firstDay: 1,
        events: eventsData,
        height: 'auto',
        dateClick: function(info) {
            if (isOwner) {
                abrirModalDisponibilidad(info.dateStr);
            }
        },
        eventClick: function(info) {
            if (isOwner) {
                abrirModalDisponibilidad(info.event.startStr, true);
            }
        }
    });
    
    calendar.render();
});

function abrirModalDisponibilidad(fecha = '', isExisting = false) {
    const modal = document.getElementById('modal-disponibilidad');
    const inputFecha = document.getElementById('input-fecha');
    const btnEliminar = document.getElementById('btn-eliminar-disp');
    
    if(fecha && inputFecha) {
        inputFecha.value = fecha;
    }
    
    if (btnEliminar) {
        if (isExisting) {
            btnEliminar.style.display = 'flex';
        } else {
            btnEliminar.style.display = 'none';
        }
    }
    
    if (modal) modal.style.display = 'flex';
}

async function eliminarDisponibilidad(btn) {
    if (!confirm("¿Seguro que quieres eliminar este día de tu disponibilidad?")) return;
    
    const form = document.getElementById('form-disponibilidad');
    const vId = form.getAttribute('data-vid');
    const fecha = document.getElementById('input-fecha').value;
    const url = '/api/v1/voluntarios/' + vId + '/disponibilidad/' + fecha;
    
    try {
        btn.style.opacity = '0.7';
        const response = await fetch(url, { method: 'DELETE' });
        if (response.ok) {
            window.location.reload();
        } else {
            alert("Error al eliminar la disponibilidad.");
            btn.style.opacity = '1';
        }
    } catch (err) {
        alert("Error de conexión al servidor backend.");
    }
}

async function guardarDisponibilidad(e, form) {
    e.preventDefault();
    
    const inputFecha = document.getElementById('input-fecha');
    const fechaValue = inputFecha ? inputFecha.value : null;
    const hoy = new Date().toISOString().split('T')[0];
    
    if (fechaValue && fechaValue < hoy) {
        alert("No puedes añadir disponibilidad para días que ya han pasado.");
        return;
    }
    
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    const vId = form.getAttribute('data-vid');
    const url = '/api/v1/voluntarios/' + vId + '/disponibilidad'; 
    
    try {
        const btn = form.querySelector('button[type="submit"]');
        if (btn) {
            btn.innerHTML = 'Guardando...';
            btn.style.opacity = '0.7';
        }
        
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            window.location.reload();
        } else {
            alert("Error al guardar la disponibilidad. Comprueba que el formato es correcto.");
            if (btn) {
                btn.innerHTML = '<i data-lucide="save" style="width: 18px;"></i> Guardar Día';
                btn.style.opacity = '1';
            }
        }
    } catch (err) { 
        alert("Error de conexión al servidor backend."); 
    }
}

// === DISPONIBILIDAD MODAL ===
function closeDisponibilidadModal(e) {
    if (e && e.target !== e.currentTarget) return;
    const container = document.getElementById('modals-container');
    if (container) container.innerHTML = '';
}

function initDisponibilidadCalendar(disponibilidadesData) {
    setTimeout(() => {
        if (window.lucide) lucide.createIcons();

        var calendarEl = document.getElementById('disponibilidad-calendar');
        if (calendarEl) {
            var events = disponibilidadesData.map(function(d) {
                var color = d.estado === 'DISPONIBLE' ? '#10b981' : '#ef4444'; // Emerald / Red
                var title = d.estado === 'DISPONIBLE' ? 'Disponible (' + d.turno + ')' : 'No Disponible';
                return {
                    title: title,
                    start: d.fecha,
                    allDay: true,
                    backgroundColor: color,
                    borderColor: color,
                    textColor: '#ffffff'
                };
            });

        var calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                locale: 'es',
                headerToolbar: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth'
                },
                buttonText: {
                    today: 'Hoy',
                    month: 'Mes'
                },
                events: events,
                height: 500
            });
            calendar.render();
        }
    }, 50);
}

// === FUNCIONES COMUNES DE FORMULARIOS (Refactorizadas) ===

// Selección genérica de animales en buscadores (usado en Adopciones, Solicitudes, Historial Medico)
function selectAnimal(id, nombre, especie, foto) {
    const idInput = document.getElementById('animalId-input');
    const searchInput = document.getElementById('animal-search');
    const suggestions = document.getElementById('animal-suggestions');
    
    if (idInput) idInput.value = id;
    if (searchInput) searchInput.value = nombre;
    if (suggestions) suggestions.innerHTML = '';
    
    // Si hay una tarjeta de previsualización del animal seleccionado (Ej: Solicitud o Adopción)
    const card = document.getElementById('selected-animal-card');
    if (card) {
        const img = document.getElementById('selected-animal-img');
        const nameTxt = document.getElementById('selected-animal-name');
        const speciesTxt = document.getElementById('selected-animal-species');
        
        if (img) img.src = (foto && foto !== 'null' && foto !== '') ? foto : '/images/placeholder-animal.png';
        if (nameTxt) nameTxt.innerText = nombre;
        if (speciesTxt) speciesTxt.innerText = especie.charAt(0).toUpperCase() + especie.slice(1).toLowerCase();
        card.style.display = 'flex';
        
        if (window.lucide) lucide.createIcons();
    }
}

function clearAnimalSelection() {
    const idInput = document.getElementById('animalId-input');
    const searchInput = document.getElementById('animal-search');
    const card = document.getElementById('selected-animal-card');
    
    if (idInput) idInput.value = '';
    if (searchInput) {
        searchInput.value = '';
        searchInput.focus();
    }
    if (card) card.style.display = 'none';
}

// Previsualización y redimensionado de imágenes subidas en formularios
function previewImage(input) {
    const preview = document.getElementById('imagePreview');
    const container = document.getElementById('imagePreviewContainer');
    const resizedInput = document.getElementById('resizedFotoInput');

    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function (e) {
            if (preview) preview.src = e.target.result;
            if (container) container.style.display = 'flex';

            const img = new Image();
            img.src = e.target.result;
            img.onload = function () {
                const canvas = document.createElement('canvas');
                const MAX_WIDTH = 800;
                const MAX_HEIGHT = 800;
                let width = img.width;
                let height = img.height;

                if (width > height) {
                    if (width > MAX_WIDTH) {
                        height *= MAX_WIDTH / width;
                        width = MAX_WIDTH;
                    }
                } else {
                    if (height > MAX_HEIGHT) {
                        width *= MAX_HEIGHT / height;
                        height = MAX_HEIGHT;
                    }
                }
                canvas.width = width;
                canvas.height = height;
                const ctx = canvas.getContext('2d');
                ctx.drawImage(img, 0, 0, width, height);
                if (resizedInput) resizedInput.value = canvas.toDataURL('image/jpeg', 0.8);
            };
        }
        reader.readAsDataURL(input.files[0]);
    }
}

// Toggle "Especie Personalizada" en formulario de Animal
function toggleEspeciePersonalizada() {
    const select = document.getElementById('especieSelect');
    const group = document.getElementById('especiePersonalizadaGroup');
    const input = document.getElementById('especiePersonalizadaInput');
    
    if (!select || !group || !input) return;

    if (select.value === 'OTRO') {
        group.style.display = 'block';
        input.setAttribute('required', 'required');
    } else {
        group.style.display = 'none';
        input.removeAttribute('required');
    }
}

// Toggle "Especialidad Custom" en formulario de Voluntario
function handleEspecialidadChange() {
    const select = document.getElementById('especialidadSelect');
    const customGroup = document.getElementById('especialidadCustomGroup');
    const customInput = document.getElementById('especialidadCustomInput');
    
    if (!select || !customGroup || !customInput) return;
    
    if (select.value === 'OTRA') {
        customGroup.style.display = 'block';
        customInput.setAttribute('name', 'especialidad');
        customInput.setAttribute('required', 'required');
        select.removeAttribute('name');
    } else {
        customGroup.style.display = 'none';
        customInput.removeAttribute('name');
        customInput.removeAttribute('required');
        select.setAttribute('name', 'especialidad');
    }
}

// Evento global para ocultar sugerencias de autocompletado (dropdowns) al hacer clic fuera
document.addEventListener('click', function(event) {
    const inputsWithSuggestions = [
        { inputId: 'animal-search', suggId: 'animal-suggestions' },
        { inputId: 'user-search', suggId: 'user-suggestions' },
        { inputId: 'encargado-search', suggId: 'encargado-suggestions' }
    ];

    inputsWithSuggestions.forEach(pair => {
        const inputEl = document.getElementById(pair.inputId);
        const suggEl = document.getElementById(pair.suggId);
        
        if (inputEl && suggEl) {
            if (!inputEl.contains(event.target) && !suggEl.contains(event.target)) {
                suggEl.innerHTML = '';
            }
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('especialidadSelect')) {
        handleEspecialidadChange();
    }
});

// --- LÓGICA DEL FORMULARIO DE TAREAS ---
function initTareaFormulario() {
    const dataEl = document.getElementById('tarea-form-data');
    if (!dataEl) return;
    
    try {
        const data = JSON.parse(dataEl.textContent);
        window.tareaAssignedIds = new Set();
        const { voluntarioNombres, tareaVolIds, preselected } = data;
        
        // 1. Cargar desde la tarea si existe
        if (tareaVolIds && Array.isArray(tareaVolIds)) {
            tareaVolIds.forEach(vid => {
                if (!vid) return;
                const id = (typeof vid === 'object' && vid !== null) ? (vid.value || vid.id) : vid;
                if (id) {
                    const name = (voluntarioNombres && voluntarioNombres[id.toString()]) 
                                 ? voluntarioNombres[id.toString()] 
                                 : ('Voluntario ' + id);
                    window.addVolunteerChip(id, name);
                }
            });
        }

        // 2. Cargar preseleccionado si existe
        if (preselected && preselected.id) {
            window.addVolunteerChip(preselected.id, preselected.nombre + ' ' + (preselected.apellido || ''));
        }
    } catch (e) {
        console.error("Error parsing tarea form data", e);
    }
}

window.toggleSearch = function(show) {
    const wrapper = document.getElementById('search-wrapper');
    const btn = document.getElementById('btn-show-search');
    const input = document.getElementById('volunteer-search-input');
    if (wrapper) wrapper.style.display = show ? 'block' : 'none';
    if (btn) btn.style.display = show ? 'none' : 'inline-flex';
    if (show && input) input.focus();
};

window.selectVolunteer = function(id, name) {
    window.addVolunteerChip(id, name);
    window.toggleSearch(false);
    const sugg = document.getElementById('volunteer-suggestions');
    const input = document.getElementById('volunteer-search-input');
    if (sugg) sugg.innerHTML = '';
    if (input) input.value = '';
};

window.addVolunteerChip = function(id, name) {
    if (!window.tareaAssignedIds) window.tareaAssignedIds = new Set();
    if (window.tareaAssignedIds.has(id)) return;
    window.tareaAssignedIds.add(id);

    const container = document.getElementById('assigned-volunteers-container');
    if (!container) return;
    const chip = document.createElement('div');
    chip.className = 'volunteer-chip';
    chip.id = `vol-chip-${id}`;
    chip.innerHTML = `
        <i data-lucide="user" style="width: 14px; color: var(--primary);"></i>
        <span>${name}</span>
        <input type="hidden" name="voluntarioIds" value="${id}">
        <button type="button" class="remove-btn" onclick="removeVolunteer(${id})">
            <i data-lucide="x" style="width: 12px;"></i>
        </button>
    `;
    container.appendChild(chip);
    if (window.lucide) lucide.createIcons();
};

window.removeVolunteer = function(id) {
    if (window.tareaAssignedIds) window.tareaAssignedIds.delete(id);
    const chip = document.getElementById(`vol-chip-${id}`);
    if (chip) chip.remove();
};

function initDateLimits() {
    const dateInput = document.querySelector('input[name="fechaLimite"]');
    if (dateInput) {
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        dateInput.min = now.toISOString().slice(0, 16);
    }
}

// Inicializar en carga y HTMX
document.addEventListener('DOMContentLoaded', function() {
    initTareaFormulario();
    initDateLimits();
});

document.addEventListener('htmx:afterSettle', function(evt) {
    initTareaFormulario();
    initDateLimits();
});

// --- LÓGICA DE COOKIE CONSENT ---
document.addEventListener('DOMContentLoaded', () => {
    const consent = localStorage.getItem('cookie-consent');
    if (!consent) {
        setTimeout(() => {
            const banner = document.getElementById('cookie-banner');
            if (banner) banner.style.display = 'block';
            if (window.lucide) lucide.createIcons();
        }, 1000);
    }
});

window.acceptCookies = function(type) {
    localStorage.setItem('cookie-consent', type);
    localStorage.setItem('cookie-consent-date', new Date().toISOString());
    
    const banner = document.getElementById('cookie-banner');
    if (banner) {
        banner.style.transition = 'all 0.5s ease';
        banner.style.opacity = '0';
        banner.style.transform = 'translate(-50%, 50px)';
        setTimeout(() => banner.remove(), 500);
    }
    
    console.log('Cookies accepted:', type);
};

window.toggleCookieSettings = function() {
    const banner = document.getElementById('cookie-banner');
    if (banner) {
        banner.style.opacity = '0';
        banner.style.pointerEvents = 'none';
    }

    if (window.Swal) {
        Swal.fire({
            title: 'Preferencias de Cookies',
            html: `
                <div style="text-align: left; font-size: 0.9rem;">
                    <p><input type="checkbox" checked disabled> <strong>Necesarias</strong> Esenciales para el funcionamiento del sitio.</p>
                    <p><input type="checkbox" id="check-analytics"> <strong>Analíticas</strong> Nos ayudan a saber qué animales se visitan más.</p>
                    <p><input type="checkbox" id="check-marketing"> <strong>Marketing</strong> Para mostrarte contenido relevante.</p>
                </div>
            `,
            confirmButtonText: 'Guardar preferencias',
            confirmButtonColor: '#15803d',
            showCancelButton: true,
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                window.acceptCookies('custom');
            } else {
                if (banner) {
                    banner.style.opacity = '1';
                    banner.style.pointerEvents = 'auto';
                }
            }
        });
    }
};

// --- LÓGICA DE SELECCIÓN DE ANIMAL ---
window.selectAnimal = function(id, nombre, especie, foto) {
    const idInput = document.getElementById('animalId-input');
    const searchInput = document.getElementById('animal-search');
    const suggContainer = document.getElementById('animal-suggestions');
    if (idInput) {
        idInput.value = id;
        idInput.dispatchEvent(new Event('change', { bubbles: true }));
        if (typeof htmx !== 'undefined' && (idInput.hasAttribute('hx-get') || idInput.hasAttribute('hx-post') || idInput.hasAttribute('hx-put') || idInput.hasAttribute('hx-patch') || idInput.hasAttribute('hx-delete'))) {
            htmx.trigger(idInput, 'change');
        }
    }
    if (searchInput) searchInput.value = nombre;
    if (suggContainer) suggContainer.innerHTML = '';
    
    const card = document.getElementById('selected-animal-card');
    const img = document.getElementById('selected-animal-img');
    const nameTxt = document.getElementById('selected-animal-name');
    const speciesTxt = document.getElementById('selected-animal-species');
    
    if (img) img.src = (foto && foto !== 'null' && foto !== '') ? foto : '/images/placeholder-animal.png';
    if (nameTxt) nameTxt.innerText = nombre;
    if (speciesTxt && especie) speciesTxt.innerText = especie.charAt(0).toUpperCase() + especie.slice(1).toLowerCase();
    
    if (card) {
        card.style.display = 'flex';
        if (window.lucide) {
            lucide.createIcons();
        }
    }
};

window.clearAnimalSelection = function() {
    const idInput = document.getElementById('animalId-input');
    const searchInput = document.getElementById('animal-search');
    const card = document.getElementById('selected-animal-card');
    
    if (idInput) {
        idInput.value = '';
        idInput.dispatchEvent(new Event('change', { bubbles: true }));
        if (typeof htmx !== 'undefined' && (idInput.hasAttribute('hx-get') || idInput.hasAttribute('hx-post') || idInput.hasAttribute('hx-put') || idInput.hasAttribute('hx-patch') || idInput.hasAttribute('hx-delete'))) {
            htmx.trigger(idInput, 'change');
        }
    }
    if (searchInput) {
        searchInput.value = '';
        searchInput.focus();
    }
    if (card) card.style.display = 'none';
};

// Ocultar sugerencias de animal al hacer clic fuera y gestionar borrado manual
document.addEventListener('click', function(event) {
    const animalSearch = document.getElementById('animal-search');
    const animalSugg = document.getElementById('animal-suggestions');
    if (animalSearch && animalSugg && !animalSearch.contains(event.target) && !animalSugg.contains(event.target)) {
        animalSugg.innerHTML = '';
    }
});

function setupAnimalSearchListeners() {
    const searchInput = document.getElementById('animal-search');
    const idInput = document.getElementById('animalId-input');
    if (searchInput && idInput) {
        if (!searchInput.dataset.hasClearListener) {
            searchInput.addEventListener('input', function() {
                if (this.value.trim() === '') {
                    idInput.value = '';
                    idInput.dispatchEvent(new Event('change', { bubbles: true }));
                    if (typeof htmx !== 'undefined' && (idInput.hasAttribute('hx-get') || idInput.hasAttribute('hx-post') || idInput.hasAttribute('hx-put') || idInput.hasAttribute('hx-patch') || idInput.hasAttribute('hx-delete'))) {
                        htmx.trigger(idInput, 'change');
                    }
                }
            });
            searchInput.dataset.hasClearListener = 'true';
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    setupAnimalSearchListeners();
});

document.body.addEventListener('htmx:afterSettle', function() {
    setupAnimalSearchListeners();
});
