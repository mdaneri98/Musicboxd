/**
 * Login Page
 * User login page with form
 * Migrated from: users/login.jsp
 */

import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { Footer } from '@/components/layout';
import { LoginForm } from '@/components/forms';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { loginAsync, selectIsAuthenticated, selectAuthError, selectAuthLoading } from '@/store/slices';
import type { LoginFormData } from '@/types';

const LoginPage = () => {
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

  const handleLogin = async (data: LoginFormData) => {
    const result = await dispatch(loginAsync({
      username: data.username,
      password: data.password,
    }));
    
    if (loginAsync.fulfilled.match(result)) {
      router.push('/');
    }
  };

  return (
    <>
      <Head>
        <title>Musicboxd - Login</title>
      </Head>
      <div className="main-container-no-sidebar">
        <header className="landing-header">
          <nav className="nav-bar">
            <div className="logo">Musicboxd</div>
            <div className="nav-links">
              <Link href="/" className="nav-link">Home</Link>
              <Link href="/music" className="nav-link">Discovery</Link>
              <Link href="/search" className="nav-link">Search</Link>
              <Link href="/login" className="nav-link">Login</Link>
              <Link href="/register" className="nav-link">Register</Link>
            </div>
          </nav>
        </header>
        <div className="auth-container">
          <div className="auth-card">
            <h1 className="auth-title">Musicboxd</h1>
            <LoginForm onSubmit={handleLogin} error={error || undefined} isLoading={isLoading} />
            <div className="auth-links">
              <Link href="/register" className="auth-link">
                Don&apos;t you have an account yet?
              </Link>
              <Link href="/forgot-password" className="auth-link">
                Change password
              </Link>
            </div>
          </div>
        </div>
        <Footer />
      </div>
    </>
  );
};

export default LoginPage;

