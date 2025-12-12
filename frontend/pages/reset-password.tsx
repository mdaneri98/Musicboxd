/**
 * Reset Password Page
 * Handles password reset via code from URL parameter
 */

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { useTranslation } from 'react-i18next';
import { Footer } from '@/components/layout';
import { passwordRepository } from '@/repositories/PasswordRepository';
import type { APIError } from '@/types';

type ResetState = 'idle' | 'loading' | 'success' | 'error' | 'invalid';

const ResetPasswordPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { code } = router.query;

  const [state, setState] = useState<ResetState>('idle');
  const [password, setPassword] = useState<string>('');
  const [repeatPassword, setRepeatPassword] = useState<string>('');
  const [message, setMessage] = useState<string>('');

  useEffect(() => {
    if (router.isReady && (!code || typeof code !== 'string')) {
      setState('invalid');
      setMessage('Invalid reset link. Please check your email and try again.');
    }
  }, [code, router.isReady]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!code || typeof code !== 'string') {
      setState('invalid');
      setMessage('Invalid reset link.');
      return;
    }

    if (!password || !repeatPassword) {
      setMessage('Please fill in all fields');
      setState('error');
      return;
    }

    if (password.length < 8) {
      setMessage('Password must be at least 8 characters long');
      setState('error');
      return;
    }

    if (password !== repeatPassword) {
      setMessage('Passwords do not match');
      setState('error');
      return;
    }

    setState('loading');
    setMessage('');

    try {
      await passwordRepository.resetPassword(code, password, repeatPassword);
      setState('success');
      setMessage('Your password has been reset successfully!');

      // Redirect to login after 3 seconds
      setTimeout(() => {
        router.push('/login');
      }, 3000);
    } catch (error) {
      console.error('Reset password error:', error);
      setState('error');
      const apiError = error as APIError;
      setMessage(
        apiError.message ||
        'Failed to reset password. The link may be expired or invalid.'
      );
    }
  };

  const renderContent = () => {
    if (state === 'success') {
      return (
        <div className="reset-content">
          <div className="reset-icon success">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M8 12l2 2 4-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <h2 className="reset-title">Password Reset!</h2>
          <p className="reset-message">{message}</p>
          <p className="reset-redirect">Redirecting to login...</p>
          <Link href="/login" className="reset-button">
            Go to Login
          </Link>
        </div>
      );
    }

    if (state === 'invalid') {
      return (
        <div className="reset-content">
          <div className="reset-icon error">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          </div>
          <h2 className="reset-title">Invalid Link</h2>
          <p className="reset-message">{message}</p>
          <Link href="/forgot-password" className="reset-button">
            Request New Link
          </Link>
        </div>
      );
    }

    return (
      <div className="reset-content">
        <div className="reset-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" strokeWidth="2"/>
            <path d="M8 11V7a4 4 0 0 1 8 0v4" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </div>
        <h2 className="reset-title">Reset Password</h2>
        <p className="reset-info">
          Enter your new password below.
        </p>

        <form onSubmit={handleSubmit} className="reset-form">
          <div className="form-group">
            <input
              type="password"
              placeholder="New password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="form-input"
              disabled={state === 'loading'}
              required
              minLength={8}
            />
          </div>

          <div className="form-group">
            <input
              type="password"
              placeholder="Confirm password"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
              className="form-input"
              disabled={state === 'loading'}
              required
              minLength={8}
            />
          </div>

          {message && state === 'error' && (
            <p className="error-message">{message}</p>
          )}

          <button
            type="submit"
            className="reset-button"
            disabled={state === 'loading'}
          >
            {state === 'loading' ? 'Resetting...' : 'Reset Password'}
          </button>
        </form>

        <Link href="/login" className="reset-link">
          Back to Login
        </Link>
      </div>
    );
  };

  return (
    <>
      <Head>
        <title>Musicboxd - Reset Password</title>
      </Head>
      <div className="main-container-no-sidebar">
        <header className="landing-header">
          <nav className="nav-bar">
            <div className="logo">Musicboxd</div>
            <div className="nav-links">
              <Link href="/" className="nav-link">Home</Link>
              <Link href="/login" className="nav-link">Login</Link>
              <Link href="/register" className="nav-link">Register</Link>
            </div>
          </nav>
        </header>

        <div className="reset-container">
          <div className="reset-card">
            {renderContent()}
          </div>
        </div>

        <Footer />
      </div>

      <style jsx>{`
        .reset-container {
          display: flex;
          justify-content: center;
          align-items: center;
          min-height: 60vh;
          padding: 2rem 1rem;
        }

        .reset-card {
          background: var(--card-bg);
          border-radius: 8px;
          padding: 3rem 2rem;
          max-width: 500px;
          width: 100%;
          text-align: center;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .reset-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 1.5rem;
        }

        .reset-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 1rem;
          color: var(--primary-color, #007bff);
        }

        .reset-icon.success {
          color: #28a745;
        }

        .reset-icon.error {
          color: #dc3545;
        }

        .reset-title {
          font-size: 1.75rem;
          font-weight: 600;
          margin: 0;
          color: var(--text-primary);
        }

        .reset-message {
          font-size: 1rem;
          color: var(--text-secondary);
          margin: 0;
          line-height: 1.6;
        }

        .reset-redirect {
          font-size: 0.875rem;
          color: var(--text-secondary);
          margin: 0.5rem 0 0 0;
        }

        .reset-info {
          font-size: 0.95rem;
          color: var(--text-secondary);
          margin: 0;
          line-height: 1.6;
        }

        .reset-form {
          width: 100%;
          display: flex;
          flex-direction: column;
          gap: 1rem;
        }

        .form-group {
          width: 100%;
        }

        .form-input {
          width: 100%;
          padding: 0.75rem;
          border: 1px solid var(--border-color, #ddd);
          border-radius: 4px;
          font-size: 1rem;
          background: var(--input-bg, white);
          color: var(--text-primary);
          box-sizing: border-box;
        }

        .form-input:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .form-input:focus {
          outline: none;
          border-color: var(--primary-color, #007bff);
        }

        .error-message {
          font-size: 0.875rem;
          color: #dc3545;
          background: rgba(220, 53, 69, 0.1);
          padding: 0.75rem;
          border-radius: 4px;
          margin: 0;
        }

        .reset-button {
          margin-top: 0.5rem;
          padding: 0.75rem 2rem;
          background: var(--primary-color, #007bff);
          color: white;
          border: none;
          border-radius: 4px;
          font-size: 1rem;
          cursor: pointer;
          text-decoration: none;
          display: inline-block;
          transition: background-color 0.2s;
        }

        .reset-button:hover:not(:disabled) {
          background: var(--primary-color-dark, #0056b3);
        }

        .reset-button:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .reset-link {
          color: var(--primary-color, #007bff);
          text-decoration: none;
          font-size: 0.95rem;
          margin-top: 1rem;
        }

        .reset-link:hover {
          text-decoration: underline;
        }
      `}</style>
    </>
  );
};

export default ResetPasswordPage;
