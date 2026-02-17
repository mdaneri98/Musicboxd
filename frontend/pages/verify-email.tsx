import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { Footer } from '@/components/layout';
import { emailRepository } from '@/repositories';
import type { APIError } from '@/types';
import { useTranslation } from 'react-i18next';

type VerificationState = 'idle' | 'loading' | 'success' | 'error' | 'invalid';

const VerifyEmailPage = () => {
  const router = useRouter();
  const { code, userId } = router.query;
  const { t } = useTranslation();

  const [state, setState] = useState<VerificationState>('loading');
  const [message, setMessage] = useState<string>('');

  useEffect(() => {
    const verifyEmail = async () => {
      if (!code || typeof code !== 'string' || !userId || typeof userId !== 'string') {
        setState('invalid');
        setMessage(t('auth.verifyEmail.errors.invalidLink'));
        return;
      }

      try {
        await emailRepository.verifyEmail(Number(userId), code);
        setState('success');
        setMessage(t('auth.verifyEmail.success'));

        // Redirect to login after 3 seconds
        setTimeout(() => {
          router.push('/login');
        }, 3000);
      } catch (error) {
        console.error('Verification error:', error);
        setState('error');
        const apiError = error as APIError;
        setMessage(
          apiError.message ||
          t('auth.verifyEmail.errors.verificationFailed')
        );
      }
    };

    if (router.isReady) {
      verifyEmail();
    }
  }, [code, router, t]);

  const renderContent = () => {
    switch (state) {
      case 'loading':
        return (
          <div className="verification-content">
            <div className="verification-icon loading">
              <svg className="spinner" width="64" height="64" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round" strokeDasharray="31.4 31.4" transform="rotate(-90 12 12)">
                  <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite" />
                </circle>
              </svg>
            </div>
            <h2 className="verification-title">{t('auth.verifyEmail.verifying')}</h2>
            <p className="verification-message">{t('auth.verifyEmail.pleaseWait')}</p>
          </div>
        );

      case 'success':
        return (
          <div className="verification-content">
            <div className="verification-icon success">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" />
                <path d="M8 12l2 2 4-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </div>
            <h2 className="verification-title">{t('auth.verifyEmail.title')}</h2>
            <p className="verification-message">{message}</p>
            <p className="verification-redirect">{t('auth.verifyEmail.redirecting')}</p>
            <Link href="/login" className="btn btn-primary btn-block">
              {t('auth.verifyEmail.goToLogin')}
            </Link>
          </div>
        );

      case 'error':
      case 'invalid':
        return (
          <div className="verification-content">
            <div className="verification-icon error">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" />
                <path d="M12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
              </svg>
            </div>
            <h2 className="verification-title">{t('auth.verifyEmail.failedTitle')}</h2>
            <p className="verification-message">{message}</p>

            <Link href="/login" className="btn btn-primary btn-block">
              {t('auth.verifyEmail.backToLogin')}
            </Link>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <>
      <Head>
        <title>{t('auth.verifyEmail.pageTitle')}</title>
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

        <div className="verification-container">
          <div className="verification-card">
            {renderContent()}
          </div>
        </div>

        <Footer />
      </div>

      <style jsx>{`
        .verification-container {
          display: flex;
          justify-content: center;
          align-items: center;
          min-height: 60vh;
          padding: 2rem 1rem;
        }

        .verification-card {
          background: var(--card-bg);
          border-radius: 8px;
          padding: 3rem 2rem;
          max-width: 500px;
          width: 100%;
          text-align: center;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .verification-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 1.5rem;
        }

        .verification-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 1rem;
        }

        .verification-icon.loading {
          color: var(--primary-color, #007bff);
        }

        .verification-icon.success {
          color: #28a745;
        }

        .verification-icon.error {
          color: #dc3545;
        }

        .verification-title {
          font-size: 1.75rem;
          font-weight: 600;
          margin: 0;
          color: var(--text-primary);
        }

        .verification-message {
          font-size: 1rem;
          color: var(--text-secondary);
          margin: 0;
          line-height: 1.6;
        }

        .verification-redirect {
          font-size: 0.875rem;
          color: var(--text-secondary);
          margin: 0.5rem 0 0 0;
        }

        .verification-button {
          margin-top: 1rem;
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

        .verification-button:hover {
          background: var(--primary-color-dark, #0056b3);
        }

        .verification-link {
          color: var(--primary-color, #007bff);
          text-decoration: none;
          font-size: 0.95rem;
          margin-top: 1rem;
        }

        .verification-link:hover {
          text-decoration: underline;
        }

        .spinner {
          animation: spin 1s linear infinite;
        }

        @keyframes spin {
          from {
            transform: rotate(0deg);
          }
          to {
            transform: rotate(360deg);
          }
        }
      `}</style>
    </>
  );
};

export default VerifyEmailPage;
