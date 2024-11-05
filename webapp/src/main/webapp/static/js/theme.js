// Función para aplicar el tema
function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
}

// Aplicar el tema inicial cuando carga el documento
document.addEventListener('DOMContentLoaded', () => {
    // Verificar si hay un usuario logueado (el body tendrá el atributo data-user-theme)
    const userTheme = document.body.getAttribute('data-user-theme');
    
    // Si no hay usuario logueado o no tiene tema, usar dark
    if (!userTheme || userTheme === 'null') {
        applyTheme('dark');
    } else {
        applyTheme(userTheme);
    }

    // Escuchar cambios en el selector de tema
    const themeSelect = document.querySelector('.theme-select');
    if (themeSelect) {
        themeSelect.value = userTheme || 'dark';
        
        themeSelect.addEventListener('change', function() {
            applyTheme(this.value);
        });
    }
});

// Aplicar tema oscuro inmediatamente al cargar
(function() {
    // Verificar si hay un usuario logueado
    const userTheme = document.body.getAttribute('data-user-theme');
    
    // Si no hay usuario logueado o no tiene tema, usar dark
    if (!userTheme || userTheme === 'null') {
        applyTheme('dark');
    } else {
        applyTheme(userTheme);
    }
})();