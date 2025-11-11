import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { artistRepository, reviewRepository, imageRepository } from '@/repositories';
import type { Artist, ReviewFormData } from '@/types';

const ArtistReviewPage = () => {
  const router = useRouter();
  const { id, edit } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [artist, setArtist] = useState<Artist | null>(null);
  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const isEditMode = edit === 'true';

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      if (!id) return;

      try {
        setLoading(true);
        const artistId = parseInt(id as string);
        const artistData = await artistRepository.getArtistById(artistId);
        setArtist(artistData);

        // Check if user already reviewed this artist
        if (currentUser) {
          const reviews = await artistRepository.getArtistReviews(artistId, 0, 100);
          const userReview = reviews.items.find(r => r.userId === currentUser.id);
          
          if (userReview && !isEditMode) {
            // User already reviewed, redirect to edit
            router.push(`/artists/${id}/reviews?edit=true`);
            return;
          }
          
          if (userReview) {
            setExistingReview(userReview);
          } else if (isEditMode) {
            // No review to edit, redirect to create
            router.push(`/artists/${id}/reviews`);
            return;
          }
        }
      } catch (error) {
        console.error('Failed to fetch artist:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, isAuthenticated, currentUser, isEditMode, router]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!artist) return;

    try {
      setSubmitLoading(true);

      if (isEditMode && existingReview) {
        // Update existing review
        await reviewRepository.updateReview(existingReview.id, {
          title: data.title,
          description: data.description,
          rating: data.rating,
        });
      } else {
        // Create new review
        await artistRepository.createArtistReview(artist.id, {
          title: data.title,
          description: data.description,
          rating: data.rating,
        });
      }

      // Redirect to artist page
      router.push(`/artists/${artist.id}`);
    } catch (error) {
      console.error('Failed to submit review:', error);
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!existingReview) return;

    try {
      await reviewRepository.deleteReview(existingReview.id);
      router.push(`/artists/${id}`);
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (!isAuthenticated) {
    return null; // Will redirect
  }

  if (loading || !artist) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading...</div>
        </div>
      </Layout>
    );
  }

  const artistImgUrl = artist.imageId ? imageRepository.getImageUrl(artist.imageId) : '/assets/default-artist.png';

  return (
    <Layout title={`Musicboxd - Review ${artist.name}`}>
      <div className="content-wrapper">
        <h1 className="page-title">
          {isEditMode ? 'Edit Your Review' : 'Make a Review'}
        </h1>

        {/* Artist Preview */}
        <div className="review-preview">
          <Link href={`/artists/${artist.id}`} className="review-preview-link">
            <img src={artistImgUrl} alt={artist.name} className="review-preview-image" />
            <div className="review-preview-info">
              <h2 className="review-preview-title">{artist.name}</h2>
              {artist.bio && <p className="review-preview-subtitle">{artist.bio}</p>}
            </div>
          </Link>
        </div>

        {/* Review Form */}
        <div className="review-section">
          <ReviewForm
            onSubmit={handleSubmit}
            onCancel={() => router.push(`/artists/${artist.id}`)}
            isLoading={submitLoading}
            defaultValues={existingReview ? {
              title: existingReview.title,
              description: existingReview.description,
              rating: existingReview.rating,
            } : undefined}
          />

          {isEditMode && existingReview && (
            <div className="delete-review-container">
              <button
                type="button"
                onClick={() => setShowDeleteModal(true)}
                className="btn btn-danger"
              >
                Delete Review
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        message="Are you sure you want to delete this review?"
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteModal(false)}
        confirmText="Yes"
        cancelText="No"
      />
    </Layout>
  );
};

export default ArtistReviewPage;

