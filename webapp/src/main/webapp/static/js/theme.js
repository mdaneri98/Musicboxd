(function() {
    const currentTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', currentTheme);
})();

document.addEventListener('DOMContentLoaded', () => {
    const themeButtons = document.querySelectorAll('.theme-btn');
    if (themeButtons.length > 0) {
        const currentTheme = localStorage.getItem('theme') || 'dark';
        const themes = ['dark', 'sepia', 'ocean', 'forest', 'kawaii'];
        const themeSelector = document.querySelector('.theme-selector');
        
        themeSelector.innerHTML = '';
        
        themes.forEach(theme => {
            const button = document.createElement('button');
            button.className = `theme-btn ${theme === currentTheme ? 'active' : ''}`;
            button.setAttribute('data-theme', theme);
            
            const icon = document.createElement('i');
            switch(theme) {
                case 'dark':
                    icon.className = 'fas fa-moon';
                    break;
                case 'sepia':
                    icon.className = 'fas fa-book';
                    break;
                case 'ocean':
                    icon.className = 'fas fa-water';
                    break;
                case 'forest':
                    icon.className = 'fas fa-tree';
                    break;
                case 'kawaii':
                    icon.className = 'fas fa-heart';
                    break;
            }
            
            button.appendChild(icon);
            button.appendChild(document.createTextNode(` ${theme.charAt(0).toUpperCase() + theme.slice(1)}`));
            themeSelector.appendChild(button);
            
            button.addEventListener('click', () => {
                document.documentElement.setAttribute('data-theme', theme);
                localStorage.setItem('theme', theme);

                document.querySelectorAll('.theme-btn').forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');
            });
        });
    }
});