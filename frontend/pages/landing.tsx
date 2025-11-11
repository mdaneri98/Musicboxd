/**
 * Landing Page
 * Anonymous home page with features and popular reviews
 * Migrated from: anonymous/home.jsp
 */

import { useEffect, useState } from 'react';
import Link from 'next/link';
import Head from 'next/head';
import { Footer } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { reviewRepository } from '@/repositories';
import type { Review } from '@/types';

const LandingPage = () => {
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
              <Link href="/landing" className="nav-link">Home</Link>
              <Link href="/music" className="nav-link">Discovery</Link>
              <Link href="/search" className="nav-link">Search</Link>
              <Link href="/login" className="nav-link">Login</Link>
              <Link href="/register" className="nav-link">Register</Link>
            </div>
          </nav>
        </header>

        <main className="content-wrapper">
          <section className="hero-section">
            <h1>Discover Music</h1>
            <p>Share your passion for music. Rate, review, and discover new albums, artists, and songs.</p>
            <Link href="/register" className="btn btn-primary">
              Get Started
            </Link>
          </section>

          <section className="features-grid">
            <div className="feature-card">
              <img src="/assets/reviewIcon.png" alt="Review Icon" className="feature-icon" />
              <h3>Write Reviews</h3>
              <p>Share your thoughts on albums, artists, and songs with the community.</p>
            </div>
            <div className="feature-card">
              <img src="/assets/communityIcon.png" alt="Community Icon" className="feature-icon" />
              <h3>Join Community</h3>
              <p>Connect with music lovers, follow users, and discover new perspectives.</p>
            </div>
            <div className="feature-card">
              <img src="/assets/discoverIcon.png" alt="Discover Icon" className="feature-icon" />
              <h3>Discover Music</h3>
              <p>Explore our extensive database of artists, albums, and songs.</p>
            </div>
          </section>

          <section className="reviews-section">
            <h2>Popular Reviews</h2>
            {loading ? (
              <div className="loading">Loading popular reviews...</div>
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

