
const pageInformation = {
    inicio: {
        title: "Inicio",
        subtitle: "Resumen general del sistema"
    },
    productos: {
        title: "Productos",
        subtitle: "Administración del catálogo"
    },
    inventario: {
        title: "Inventario",
        subtitle: "Control de existencias"
    },
    ventas: {
        title: "Ventas",
        subtitle: "Administración de operaciones comerciales"
    },
    entregas: {
        title: "Entregas",
        subtitle: "Seguimiento de envíos y retiros"
    },
    reportes: {
        title: "Reportes",
        subtitle: "Consultas y estadísticas"
    }
};

const navigationLinks = document.querySelectorAll(".nav-link-custom");
const views = document.querySelectorAll(".view-section");

const pageTitle = document.getElementById("pageTitle");
const pageSubtitle = document.getElementById("pageSubtitle");

navigationLinks.forEach(link => {
    link.addEventListener("click", () => {
        const viewName = link.dataset.view;

        // Actualizar navegación
        navigationLinks.forEach(item => {
            item.classList.remove("active");
        });

        link.classList.add("active");

        // Mostrar la sección seleccionada
        views.forEach(view => {
            view.classList.remove("active");
        });

        document.getElementById(viewName).classList.add("active");

        // Actualizar encabezado
        pageTitle.textContent = pageInformation[viewName].title;
        pageSubtitle.textContent = pageInformation[viewName].subtitle;

        // Cerrar menú en dispositivos móviles
        document.getElementById("sidebar").classList.remove("open");
    });
});

// Menú responsive
document.getElementById("menuToggle").addEventListener("click", () => {
    document.getElementById("sidebar").classList.toggle("open");
});
