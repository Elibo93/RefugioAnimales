// --- COMPONENTS.JS ---

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


