/**
 * Theme Provider
 * Provides theme context and syncs with user preferences
 * Similar pattern to AuthProvider but for theming
 */

import React, { createContext, useContext, useEffect, useCallback } from 'react';
import { useAppSelector } from '@/store/hooks';
import { selectCurrentUser } from '@/store/slices/authSlice';
import { Theme } from '@/types/enums';
import useTheme from '@/hooks/useTheme';

// ============================================================================
// Context Types
// ============================================================================

interface ThemeContextType {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  isLoaded: boolean;
}

// ============================================================================
// Context
// ============================================================================

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

// ============================================================================
// Provider Component
// ============================================================================

interface ThemeProviderProps {
  children: React.ReactNode;
}

export const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
  const { theme, setTheme, isLoaded } = useTheme();
  const currentUser = useAppSelector(selectCurrentUser);

  // Sync theme with user preferences when user logs in or their preference changes
  useEffect(() => {
    if (currentUser?.preferred_theme && currentUser.preferred_theme !== theme) {
      setTheme(currentUser.preferred_theme as Theme);
    }
  }, [currentUser?.preferred_theme, setTheme]); // Don't include 'theme' to avoid loops

  // Memoized setTheme to prevent unnecessary re-renders
  const handleSetTheme = useCallback((newTheme: Theme) => {
    setTheme(newTheme);
  }, [setTheme]);

  return (
    <ThemeContext.Provider value={{ theme, setTheme: handleSetTheme, isLoaded }}>
      {children}
    </ThemeContext.Provider>
  );
};

// ============================================================================
// Hook to use theme context
// ============================================================================

export const useThemeContext = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useThemeContext must be used within a ThemeProvider');
  }
  return context;
};

export default ThemeProvider;
