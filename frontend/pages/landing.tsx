/**
 * Landing Page
 * Anonymous home page with features and popular reviews
 * Migrated from: anonymous/home.jsp
 */

import { useEffect, useState } from 'react';
import Link from 'next/link';
import Head from 'next/head';
import { useTranslation } from 'react-i18next';
import { Footer } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { reviewRepository } from '@/repositories';
import type { Review } from '@/types';

const LandingPage = () => {
  const { t } = useTranslation();
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPopularReviews = async () => {
      try {
        const response = await reviewRepository.getReviews(1, 6, undefined, "LIKES");
        const reviews = response.items.map((item) => item.data);
        setReviews(reviews);
      } catch (error) {
        console.error('Failed to fetch popular reviews:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPopularReviews();
  }, []);

  return (
    <>
      <Head>
        <title>Musicboxd - Discover Music</title>
      </Head>
      <div className="main-container-no-sidebar">
        <header className="landing-header">
          <nav className="nav-bar">
            <div className="logo">Musicboxd</div>
            <div className="nav-links">
              <Link href="/landing" className="nav-link">{t('navbar.home')}</Link>
              <Link href="/music" className="nav-link">{t('navbar.discovery')}</Link>
              <Link href="/search" className="nav-link">{t('navbar.search')}</Link>
              <Link href="/login" className="nav-link">{t('navbar.login')}</Link>
              <Link href="/register" className="nav-link">{t('navbar.register')}</Link>
            </div>
          </nav>
        </header>

        <main className="content-wrapper">
          <section className="hero-section">
            <h1>{t('landing.title')}</h1>
            <p>{t('landing.subtitle')}</p>
            <Link href="/register" className="btn btn-primary">
              {t('landing.getStarted')}
            </Link>
          </section>

          <section className="features-grid">
            <div className="feature-card">
              <img src="/assets/reviewIcon.png" alt="Review Icon" className="feature-icon" />
              <h3>{t('landing.features.writeReviews.title')}</h3>
              <p>{t('landing.features.writeReviews.description')}</p>
            </div>
            <div className="feature-card">
              <img src="/assets/communityIcon.png" alt="Community Icon" className="feature-icon" />
              <h3>{t('landing.features.joinCommunity.title')}</h3>
              <p>{t('landing.features.joinCommunity.description')}</p>
            </div>
            <div className="feature-card">
              <img src="/assets/discoverIcon.png" alt="Discover Icon" className="feature-icon" />
              <h3>{t('landing.features.discoverMusic.title')}</h3>
              <p>{t('landing.features.discoverMusic.description')}</p>
            </div>
          </section>

          <section className="reviews-section">
            <h2>{t('landing.popularReviews')}</h2>
            {loading ? (
              <div className="loading">{t('landing.loading')}</div>
            ) : (
              <div className="reviews-grid">
                {reviews.map((review) => (
                  <ReviewCard key={review.id} review={review} />
                ))}
              </div>
            )}
          </section>
          <Footer />
        </main>
      </div>
    </>
  );
};

export default LandingPage;

