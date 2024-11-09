
function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('cached-theme', theme);
}

function getCachedTheme() {
    return localStorage.getItem('cached-theme') || 'dark';
}

document.addEventListener('DOMContentLoaded', () => {
    const userTheme = document.body.getAttribute('data-user-theme');
    const cachedTheme = getCachedTheme();
    
    if (userTheme && userTheme !== 'null' && userTheme !== cachedTheme) {
        applyTheme(userTheme);
    } else {
        applyTheme(cachedTheme);
    }

    const themeSelect = document.querySelector('.theme-select');
    if (themeSelect) {
        themeSelect.value = userTheme || cachedTheme;
        
        themeSelect.addEventListener('change', function() {
            applyTheme(this.value);
        });
    }
});

(function() {
    const userTheme = document.body.getAttribute('data-user-theme');
    const cachedTheme = getCachedTheme();
    
    if (userTheme && userTheme !== 'null' && userTheme !== cachedTheme) {
        applyTheme(userTheme);
    } else {
        applyTheme(cachedTheme);
    }
})();