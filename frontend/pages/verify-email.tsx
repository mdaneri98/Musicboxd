/**
 * Email Verification Page
 * Handles email verification via code from URL parameter
 */

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import Head from 'next/head';
import { Footer } from '@/components/layout';
import { emailRepository } from '@/repositories';
import type { APIError } from '@/types';

type VerificationState = 'loading' | 'success' | 'error' | 'invalid';

const VerifyEmailPage = () => {
  const router = useRouter();
  const { code } = router.query;

  const [state, setState] = useState<VerificationState>('loading');
  const [message, setMessage] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [resendLoading, setResendLoading] = useState(false);
  const [resendMessage, setResendMessage] = useState<string>('');

  useEffect(() => {
    const verifyEmail = async () => {
      if (!code || typeof code !== 'string') {
        setState('invalid');
        setMessage('Invalid verification link. Please check your email and try again.');
        return;
      }

      try {
        await emailRepository.verifyEmail(code);
        setState('success');
        setMessage('Your email has been verified successfully!');

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
          'Failed to verify email. The link may be expired or invalid.'
        );
      }
    };

    if (router.isReady) {
      verifyEmail();
    }
  }, [code, router]);

  const handleResendVerification = async () => {
    if (!email) {
      setResendMessage('Please enter your email address');
      return;
    }

    setResendLoading(true);
    setResendMessage('');

    try {
      await emailRepository.resendVerification(email);
      setResendMessage('Verification email has been resent. Please check your inbox.');
    } catch (error) {
      console.error('Resend error:', error);
      const apiError = error as APIError;
      setResendMessage(
        apiError.message ||
        'Failed to resend verification email. Please try again later.'
      );
    } finally {
      setResendLoading(false);
    }
  };

  const renderContent = () => {
    switch (state) {
      case 'loading':
        return (
          <div className="verification-content">
            <div className="verification-icon loading">
              <svg className="spinner" width="64" height="64" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round" strokeDasharray="31.4 31.4" transform="rotate(-90 12 12)">
                  <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                </circle>
              </svg>
            </div>
            <h2 className="verification-title">Verifying your email...</h2>
            <p className="verification-message">Please wait while we verify your email address.</p>
          </div>
        );

      case 'success':
        return (
          <div className="verification-content">
            <div className="verification-icon success">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                <path d="M8 12l2 2 4-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <h2 className="verification-title">Email Verified!</h2>
            <p className="verification-message">{message}</p>
            <p className="verification-redirect">Redirecting to login...</p>
            <Link href="/login" className="verification-button">
              Go to Login
            </Link>
          </div>
        );

      case 'error':
      case 'invalid':
        return (
          <div className="verification-content">
            <div className="verification-icon error">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
                <path d="M12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
            </div>
            <h2 className="verification-title">Verification Failed</h2>
            <p className="verification-message">{message}</p>

            <div className="resend-section">
              <p className="resend-label">Need a new verification link?</p>
              <div className="resend-form">
                <input
                  type="email"
                  placeholder="Enter your email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="resend-input"
                  disabled={resendLoading}
                />
                <button
                  onClick={handleResendVerification}
                  className="resend-button"
                  disabled={resendLoading}
                >
                  {resendLoading ? 'Sending...' : 'Resend Email'}
                </button>
              </div>
              {resendMessage && (
                <p className={`resend-message ${resendMessage.includes('Failed') ? 'error' : 'success'}`}>
                  {resendMessage}
                </p>
              )}
            </div>

            <Link href="/login" className="verification-link">
              Back to Login
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
        <title>Musicboxd - Email Verification</title>
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

        .resend-section {
          margin-top: 2rem;
          padding-top: 2rem;
          border-top: 1px solid var(--border-color, #ddd);
          width: 100%;
        }

        .resend-label {
          font-size: 0.95rem;
          color: var(--text-primary);
          margin-bottom: 1rem;
        }

        .resend-form {
          display: flex;
          gap: 0.5rem;
          margin-bottom: 1rem;
        }

        .resend-input {
          flex: 1;
          padding: 0.75rem;
          border: 1px solid var(--border-color, #ddd);
          border-radius: 4px;
          font-size: 0.95rem;
          background: var(--input-bg, white);
          color: var(--text-primary);
        }

        .resend-input:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .resend-button {
          padding: 0.75rem 1.5rem;
          background: var(--primary-color, #007bff);
          color: white;
          border: none;
          border-radius: 4px;
          font-size: 0.95rem;
          cursor: pointer;
          white-space: nowrap;
          transition: background-color 0.2s;
        }

        .resend-button:hover:not(:disabled) {
          background: var(--primary-color-dark, #0056b3);
        }

        .resend-button:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .resend-message {
          font-size: 0.875rem;
          margin: 0;
          padding: 0.75rem;
          border-radius: 4px;
        }

        .resend-message.success {
          color: #28a745;
          background: rgba(40, 167, 69, 0.1);
        }

        .resend-message.error {
          color: #dc3545;
          background: rgba(220, 53, 69, 0.1);
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
