import { Component, ReactNode, ErrorInfo } from 'react';
import Link from 'next/link';

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
  errorInfo?: ErrorInfo;
}

/**
 * ErrorBoundary Component
 * Catches JavaScript errors anywhere in the child component tree and displays a fallback UI
 */
class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
    };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    // Update state so the next render will show the fallback UI
    return {
      hasError: true,
      error,
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    // Log the error to an error reporting service
    console.error('ErrorBoundary caught an error:', error, errorInfo);

    this.setState({
      error,
      errorInfo,
    });
  }

  render(): ReactNode {
    if (this.state.hasError) {
      // Custom fallback UI
      if (this.props.fallback) {
        return this.props.fallback;
      }

      // Default fallback UI
      return (
        <div className="main-container">
          <main className="content-wrapper">
            <div className="error-page">
              <div className="error-content">
                <h1 className="error-code">Oops!</h1>
                <h2 className="error-title">Something went wrong</h2>
                <p className="error-message">
                  We're sorry, but something unexpected happened. Please try refreshing the page or go back to the home page.
                </p>
                
                {process.env.NODE_ENV === 'development' && this.state.error && (
                  <details style={{ whiteSpace: 'pre-wrap', marginTop: '2rem' }}>
                    <summary style={{ cursor: 'pointer', marginBottom: '1rem' }}>
                      Error Details (Development Only)
                    </summary>
                    <div style={{ 
                      backgroundColor: '#f5f5f5', 
                      padding: '1rem', 
                      borderRadius: '4px',
                      fontSize: '0.875rem',
                      overflow: 'auto'
                    }}>
                      <strong>Error:</strong> {this.state.error.toString()}
                      <br />
                      <br />
                      <strong>Stack Trace:</strong>
                      <pre>{this.state.error.stack}</pre>
                      {this.state.errorInfo && (
                        <>
                          <strong>Component Stack:</strong>
                          <pre>{this.state.errorInfo.componentStack}</pre>
                        </>
                      )}
                    </div>
                  </details>
                )}

                <div style={{ marginTop: '2rem', display: 'flex', gap: '1rem' }}>
                  <button 
                    onClick={() => window.location.reload()} 
                    className="btn btn-secondary"
                  >
                    Refresh Page
                  </button>
                  <Link href="/" className="btn btn-primary">
                    Back to Home
                  </Link>
                </div>
              </div>
            </div>
          </main>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;

