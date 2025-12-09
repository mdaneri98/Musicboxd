import type { AppProps } from 'next/app';
import { Provider } from 'react-redux';
import { store } from '@/store';
import ErrorBoundary from '@/components/ErrorBoundary';
import AuthProvider from '@/components/AuthProvider';
import ThemeProvider from '@/components/ThemeProvider';
import '../i18n'; // Import i18n configuration

// Import existing CSS files in correct order
import '@/styles/base.css';
import '@/styles/layout.css';
import '@/styles/components.css';
import '@/styles/modules.css';

export default function App({ Component, pageProps }: AppProps) {
  return (
    <Provider store={store}>
      <AuthProvider>
        <ThemeProvider>
          <ErrorBoundary>
            <Component {...pageProps} />
          </ErrorBoundary>
        </ThemeProvider>
      </AuthProvider>
    </Provider>
  );
}

