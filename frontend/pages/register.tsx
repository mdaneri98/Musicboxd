/**
 * Register Page
 * User registration page with form
 * Migrated from: users/register.jsp
 */

import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { useTranslation } from 'react-i18next';
import { Footer } from '@/components/layout';
import { RegisterForm } from '@/components/forms';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { registerAsync, selectIsAuthenticated, selectAuthError, selectAuthLoading } from '@/store/slices';
import type { RegisterFormData } from '@/types';

const RegisterPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const error = useAppSelector(selectAuthError);
  const isLoading = useAppSelector(selectAuthLoading);

  useEffect(() => {
    if (isAuthenticated) {
      router.push('/');
    }
  }, [isAuthenticated, router]);

  const handleRegister = async (data: RegisterFormData) => {
    await dispatch(registerAsync(data)).unwrap();
    router.push('/');
  };

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
            <h1 className="auth-title">Musicboxd</h1>
            <RegisterForm onSubmit={handleRegister} error={error || undefined} isLoading={isLoading} />
            <div className="auth-links">
              <Link href="/login" className="auth-link">
                {t('auth.register.haveAccount')}
              </Link>
            </div>
          </div>
        </div>
        <Footer />
      </div>
    </>
  );
};

export default RegisterPage;

