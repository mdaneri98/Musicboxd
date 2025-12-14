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
      setMessage(t('auth.resetPassword.errors.invalidLinkDetailed'));
    }
  }, [code, router.isReady, t]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!code || typeof code !== 'string') {
      setState('invalid');
      setMessage(t('auth.resetPassword.errors.invalidLink'));
      return;
    }

    if (!password || !repeatPassword) {
      setMessage(t('auth.resetPassword.errors.fillAllFields'));
      setState('error');
      return;
    }

    if (password.length < 8) {
      setMessage(t('auth.resetPassword.errors.passwordMinLength'));
      setState('error');
      return;
    }

    if (password !== repeatPassword) {
      setMessage(t('auth.resetPassword.errors.passwordMismatch'));
      setState('error');
      return;
    }

    setState('loading');
    setMessage('');

    try {
      await passwordRepository.resetPassword(code, password, repeatPassword);
      setState('success');
      setMessage(t('auth.resetPassword.success'));

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
        t('auth.resetPassword.errors.resetFailed')
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
          <h1 className="auth-title">{t('auth.resetPassword.title')}</h1>
          <div className="alert alert-success">
            {message}
          </div>
          <p style={{ color: 'var(--color-accent)', textAlign: 'center', marginBottom: 'var(--space-lg)' }}>
            {t('auth.resetPassword.redirecting')}
          </p>
          <Link href="/login" className="btn btn-primary btn-block">
            {t('auth.resetPassword.goToLogin')}
          </Link>
        </>
      );
    }

    if (state === 'invalid') {
      return (
        <>
          <div className="status-icon error">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          </div>
          <h1 className="auth-title">{t('auth.resetPassword.invalidLinkTitle')}</h1>
          <div className="alert alert-danger">
            {message}
          </div>
          <Link href="/forgot-password" className="btn btn-primary btn-block">
            {t('auth.resetPassword.requestNewLink')}
          </Link>
        </>
      );
    }

    return (
      <>
        <div className="status-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" strokeWidth="2"/>
            <path d="M8 11V7a4 4 0 0 1 8 0v4" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </div>
        <h1 className="auth-title">{t('auth.resetPassword.heading')}</h1>
        <p style={{ color: 'var(--color-text-secondary)', textAlign: 'center', marginBottom: 'var(--space-lg)' }}>
          {t('auth.resetPassword.subtitle')}
        </p>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <input
              type="password"
              placeholder={t('auth.resetPassword.newPassword')}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="form-control"
              disabled={state === 'loading'}
              required
              minLength={8}
            />
          </div>

          <div className="form-group">
            <input
              type="password"
              placeholder={t('auth.resetPassword.confirmPassword')}
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
              className="form-control"
              disabled={state === 'loading'}
              required
              minLength={8}
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
            {state === 'loading' ? t('auth.resetPassword.resetting') : t('auth.resetPassword.submit')}
          </button>
        </form>

        <div className="auth-links">
          <Link href="/login" className="auth-link">
            {t('auth.resetPassword.backToLogin')}
          </Link>
        </div>
      </>
    );
  };

  return (
    <>
      <Head>
        <title>{t('auth.resetPassword.pageTitle')}</title>
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

        .status-icon.error {
          background: linear-gradient(135deg, var(--color-danger) 0%, var(--color-danger-hover) 100%);
        }
      `}</style>
    </>
  );
};

export default ResetPasswordPage;
