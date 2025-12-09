/**
 * useTheme Hook
 * Manages theme state with localStorage persistence and DOM updates
 * Follows the same pattern as the legacy webapp theme.js
 */

import { useState, useEffect, useCallback } from 'react';
import { Theme } from '@/types/enums';

const THEME_STORAGE_KEY = 'cached-theme';
const DEFAULT_THEME = Theme.DARK;

/**
 * Apply theme to the document root element
 * This mirrors the webapp's applyTheme function
 */
const applyThemeToDOM = (theme: Theme): void => {
  if (typeof document !== 'undefined') {
    document.documentElement.setAttribute('data-theme', theme);
  }
};

/**
 * Get cached theme from localStorage
 * Returns default theme if not found or on SSR
 */
const getCachedTheme = (): Theme => {
  if (typeof window === 'undefined') {
    return DEFAULT_THEME;
  }
  
  const cached = localStorage.getItem(THEME_STORAGE_KEY);
  if (cached && Object.values(Theme).includes(cached as Theme)) {
    return cached as Theme;
  }
  return DEFAULT_THEME;
};

/**
 * Save theme to localStorage
 */
const cacheTheme = (theme: Theme): void => {
  if (typeof window !== 'undefined') {
    localStorage.setItem(THEME_STORAGE_KEY, theme);
  }
};

export interface UseThemeReturn {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  isLoaded: boolean;
}

/**
 * Hook to manage application theme
 * - Persists theme to localStorage
 * - Syncs with user preferences when logged in
 * - Applies theme immediately to avoid flash
 */
export const useTheme = (): UseThemeReturn => {
  const [theme, setThemeState] = useState<Theme>(DEFAULT_THEME);
  const [isLoaded, setIsLoaded] = useState(false);

  // Initialize theme from localStorage on mount
  useEffect(() => {
    const cachedTheme = getCachedTheme();
    setThemeState(cachedTheme);
    applyThemeToDOM(cachedTheme);
    setIsLoaded(true);
  }, []);

  // Function to update theme
  const setTheme = useCallback((newTheme: Theme) => {
    setThemeState(newTheme);
    applyThemeToDOM(newTheme);
    cacheTheme(newTheme);
  }, []);

  return {
    theme,
    setTheme,
    isLoaded,
  };
};

export default useTheme;
