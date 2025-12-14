/**
 * Forgot Password Page
 * Allows users to request a password reset email
 */

import { useState } from 'react';
import Link from 'next/link';
import Head from 'next/head';
import { useTranslation } from 'react-i18next';
import { Footer } from '@/components/layout';
import { passwordRepository } from '@/repositories/PasswordRepository';
import type { APIError } from '@/types';

type RequestState = 'idle' | 'loading' | 'success' | 'error';

const ForgotPasswordPage = () => {
  const { t } = useTranslation();

  const [email, setEmail] = useState<string>('');
  const [state, setState] = useState<RequestState>('idle');
  const [message, setMessage] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email) {
      setMessage(t('auth.forgotPassword.errors.emailRequired'));
      setState('error');
      return;
    }

    setState('loading');
    setMessage('');

    try {
      await passwordRepository.forgotPassword(email);
      setState('success');
      setMessage(t('auth.forgotPassword.success'));
    } catch (error) {
      console.error('Forgot password error:', error);
      setState('error');
      const apiError = error as APIError;
      setMessage(
        apiError.message ||
        t('auth.forgotPassword.errors.sendFailed')
      );
    }
  };

  const renderContent = () => {
    if (state === 'success') {
      return (
        <>
          <div className="status-icon success">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M8 12l2 2 4-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <h1 className="auth-title">{t('auth.forgotPassword.emailSentTitle')}</h1>
          <div className="alert alert-success">
            {message}
          </div>
          <p style={{ color: 'var(--color-text-secondary)', textAlign: 'center', marginBottom: 'var(--space-lg)' }}>
            {t('auth.forgotPassword.checkEmail')}
          </p>
          <Link href="/login" className="btn btn-primary btn-block">
            {t('auth.forgotPassword.backToLogin')}
          </Link>
        </>
      );
    }

    return (
      <>
        <div className="status-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
            <path d="M12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </div>
        <h1 className="auth-title">{t('auth.forgotPassword.title')}</h1>
        <p style={{ color: 'var(--color-text-secondary)', textAlign: 'center', marginBottom: 'var(--space-lg)' }}>
          {t('auth.forgotPassword.subtitle')}
        </p>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <input
              type="email"
              placeholder={t('auth.forgotPassword.emailPlaceholder')}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="form-control"
              disabled={state === 'loading'}
              required
            />
          </div>

          {message && state === 'error' && (
            <div className="alert alert-danger">{message}</div>
          )}

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={state === 'loading'}
          >
            {state === 'loading' ? t('auth.forgotPassword.sending') : t('auth.forgotPassword.submit')}
          </button>
        </form>

        <div className="auth-links">
          <Link href="/login" className="auth-link">
            {t('auth.forgotPassword.backToLogin')}
          </Link>
        </div>
      </>
    );
  };

  return (
    <>
      <Head>
        <title>{t('auth.forgotPassword.pageTitle')}</title>
      </Head>
      <div className="main-container-no-sidebar">
        <header className="landing-header">
          <nav className="nav-bar">
            <div className="logo">Musicboxd</div>
            <div className="nav-links">
              <Link href="/" className="nav-link">{t('navbar.home')}</Link>
              <Link href="/login" className="nav-link">{t('navbar.login')}</Link>
              <Link href="/register" className="nav-link">{t('navbar.register')}</Link>
            </div>
          </nav>
        </header>

        <div className="auth-container">
          <div className="auth-card">
            {renderContent()}
          </div>
        </div>

        <Footer />
      </div>

      <style jsx>{`
        .status-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 80px;
          height: 80px;
          margin: 0 auto var(--space-lg);
          border-radius: 50%;
          background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-accent-gradient) 100%);
          color: var(--color-background-primary);
        }

        .status-icon.success {
          background: linear-gradient(135deg, var(--color-success) 0%, #28d966 100%);
        }
      `}</style>
    </>
  );
};

export default ForgotPasswordPage;
