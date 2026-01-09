import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { useTranslation } from 'react-i18next';
import { Footer } from '@/components/layout';
import { RegisterForm } from '@/components/forms';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { registerAsync, selectIsAuthenticated, selectAuthError, selectAuthLoading, clearAuthError } from '@/store/slices';
import type { RegisterFormData } from '@/types';

const RegisterPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const error = useAppSelector(selectAuthError);
  const isLoading = useAppSelector(selectAuthLoading);
  const [registerSuccess, setRegisterSuccess] = useState(false);

  // Clear any previous auth errors when component mounts
  useEffect(() => {
    dispatch(clearAuthError());
  }, [dispatch]);

  useEffect(() => {
    if (isAuthenticated) {
      router.push('/');
    }
  }, [isAuthenticated, router]);

  const handleRegister = async (data: RegisterFormData) => {
    try {
      await dispatch(registerAsync(data)).unwrap();
      setRegisterSuccess(true);
      // Redirect to login after 3 seconds
      setTimeout(() => {
        router.push('/login');
      }, 3000);
    } catch {
      // Error is handled by Redux state
    }
  };

  const renderSuccessContent = () => (
    <>
      <div className="status-icon success">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" />
          <path d="M8 12l2 2 4-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </div>
      <h1 className="auth-title">{t('auth.register.successTitle')}</h1>
      <p style={{ color: 'var(--color-text-secondary)', textAlign: 'center', marginBottom: 'var(--space-md)' }}>
        {t('auth.register.successMessage')}
      </p>
      <p style={{ color: 'var(--color-accent)', textAlign: 'center', marginBottom: 'var(--space-lg)' }}>
        {t('auth.register.redirecting')}
      </p>
      <Link href="/login" className="btn btn-primary btn-block">
        {t('auth.register.goToLogin')}
      </Link>
    </>
  );

  return (
    <>
      <Head>
        <title>Musicboxd - Register</title>
      </Head>
      <div className="main-container-no-sidebar">
        <header className="landing-header">
          <nav className="nav-bar">
            <div className="logo">Musicboxd</div>
            <div className="nav-links">
              <Link href="/" className="nav-link">{t('navbar.home')}</Link>
              <Link href="/music" className="nav-link">{t('navbar.discovery')}</Link>
              <Link href="/search" className="nav-link">{t('navbar.search')}</Link>
              <Link href="/login" className="nav-link">{t('navbar.login')}</Link>
              <Link href="/register" className="nav-link">{t('navbar.register')}</Link>
            </div>
          </nav>
        </header>
        <div className="auth-container">
          <div className="auth-card">
            {registerSuccess ? (
              renderSuccessContent()
            ) : (
              <>
                <h1 className="auth-title">Musicboxd</h1>
                <RegisterForm onSubmit={handleRegister} error={error || undefined} isLoading={isLoading} />
                <div className="auth-links">
                  <Link href="/login" className="auth-link">
                    {t('auth.register.haveAccount')}
                  </Link>
                </div>
              </>
            )}
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

export default RegisterPage;

