/**
 * Register Page
 * User registration page with form
 * Migrated from: users/register.jsp
 */

import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { Footer } from '@/components/layout';
import { RegisterForm } from '@/components/forms';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { registerAsync, selectIsAuthenticated, selectAuthError, selectAuthLoading } from '@/store/slices';
import type { RegisterFormData } from '@/types';

const RegisterPage = () => {
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
    const result = await dispatch(registerAsync({
      username: data.username,
      email: data.email,
      password: data.password,
    }));
    
    if (registerAsync.fulfilled.match(result)) {
      router.push('/');
    }
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
            <RegisterForm onSubmit={handleRegister} error={error || undefined} isLoading={isLoading} />
            <div className="auth-links">
              <Link href="/login" className="auth-link">
                Already have an account?
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

