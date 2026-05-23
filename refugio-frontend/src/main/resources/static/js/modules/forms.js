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
        if (filterForm) filterForm.style.display = 'none';
        
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
            if (t.estado === 'COMPLETADA' || t.estado === 'FINALIZADA') color = '#10b981';
            else if (t.estado === 'PENDIENTE' || t.estado === 'PROPUESTA') color = '#f59e0b';
            else if (t.estado === 'RECHAZADA' || t.estado === 'CANCELADA') color = '#ef4444';
            
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


