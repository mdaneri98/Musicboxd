/**
 * Forgot Password Page
 * Allows users to request a password reset email
 */

import { useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { useTranslation } from 'react-i18next';
import { Footer } from '@/components/layout';
import { passwordRepository } from '@/repositories/PasswordRepository';
import type { APIError } from '@/types';

type RequestState = 'idle' | 'loading' | 'success' | 'error';

const ForgotPasswordPage = () => {
  const { t } = useTranslation();
  const router = useRouter();

  const [email, setEmail] = useState<string>('');
  const [state, setState] = useState<RequestState>('idle');
  const [message, setMessage] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email) {
      setMessage('Please enter your email address');
      setState('error');
      return;
    }

    setState('loading');
    setMessage('');

    try {
      await passwordRepository.forgotPassword(email);
      setState('success');
      setMessage('Password reset instructions have been sent to your email.');
    } catch (error) {
      console.error('Forgot password error:', error);
      setState('error');
      const apiError = error as APIError;
      setMessage(
        apiError.message ||
        'Failed to send password reset email. Please try again later.'
      );
    }
  };

  const renderContent = () => {
    if (state === 'success') {
      return (
        <div className="password-content">
          <div className="password-icon success">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M8 12l2 2 4-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <h2 className="password-title">Email Sent!</h2>
          <p className="password-message">{message}</p>
          <p className="password-info">
            Please check your email and click on the link to reset your password.
          </p>
          <Link href="/login" className="password-button">
            Back to Login
          </Link>
        </div>
      );
    }

    return (
      <div className="password-content">
        <div className="password-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
            <path d="M12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </div>
        <h2 className="password-title">Forgot Password?</h2>
        <p className="password-info">
          Enter your email address and we'll send you a link to reset your password.
        </p>

        <form onSubmit={handleSubmit} className="password-form">
          <div className="form-group">
            <input
              type="email"
              placeholder="Email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="form-input"
              disabled={state === 'loading'}
              required
            />
          </div>

          {message && state === 'error' && (
            <p className="error-message">{message}</p>
          )}

          <button
            type="submit"
            className="password-button"
            disabled={state === 'loading'}
          >
            {state === 'loading' ? 'Sending...' : 'Send Reset Link'}
          </button>
        </form>

        <Link href="/login" className="password-link">
          Back to Login
        </Link>
      </div>
    );
  };

  return (
    <>
      <Head>
        <title>Musicboxd - Forgot Password</title>
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

        <div className="password-container">
          <div className="password-card">
            {renderContent()}
          </div>
        </div>

        <Footer />
      </div>

      <style jsx>{`
        .password-container {
          display: flex;
          justify-content: center;
          align-items: center;
          min-height: 60vh;
          padding: 2rem 1rem;
        }

        .password-card {
          background: var(--card-bg);
          border-radius: 8px;
          padding: 3rem 2rem;
          max-width: 500px;
          width: 100%;
          text-align: center;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .password-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 1.5rem;
        }

        .password-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 1rem;
          color: var(--primary-color, #007bff);
        }

        .password-icon.success {
          color: #28a745;
        }

        .password-title {
          font-size: 1.75rem;
          font-weight: 600;
          margin: 0;
          color: var(--text-primary);
        }

        .password-message {
          font-size: 1rem;
          color: var(--text-secondary);
          margin: 0;
          line-height: 1.6;
        }

        .password-info {
          font-size: 0.95rem;
          color: var(--text-secondary);
          margin: 0;
          line-height: 1.6;
        }

        .password-form {
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

        .password-button {
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

        .password-button:hover:not(:disabled) {
          background: var(--primary-color-dark, #0056b3);
        }

        .password-button:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .password-link {
          color: var(--primary-color, #007bff);
          text-decoration: none;
          font-size: 0.95rem;
          margin-top: 1rem;
        }

        .password-link:hover {
          text-decoration: underline;
        }
      `}</style>
    </>
  );
};

export default ForgotPasswordPage;
