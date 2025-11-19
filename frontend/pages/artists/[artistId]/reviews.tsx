import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  fetchArtistByIdAsync, 
  fetchArtistReviewsAsync, 
  updateReviewAsync, 
  deleteReviewAsync, 
  createArtistReviewAsync,
  selectCurrentArtist,
  selectLoadingArtist,
  clearCurrentArtist
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { ReviewFormData } from '@/types';

const ArtistReviewPage = () => {
  const router = useRouter();
  const { artistId, edit } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  // Usar selector de Redux en lugar de useState
  const artist = useAppSelector(selectCurrentArtist);
  const loadingArtist = useAppSelector(selectLoadingArtist);

  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const isEditMode = edit === 'true';

  // Limpiar el artista al desmontar
  useEffect(() => {
    return () => {
      dispatch(clearCurrentArtist());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      if (!artistId) return;

      try {
        setLoading(true);
        const artistIdNum = parseInt(artistId as string);
        await dispatch(fetchArtistByIdAsync(artistIdNum)).unwrap();

        // Check if user already reviewed this artist
        if (currentUser) {
          const reviews = await dispatch(fetchArtistReviewsAsync({ artistId: artistIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find(r => r.data.user_id === currentUser.id);
          
          if (userReview && !isEditMode) {
            // User already reviewed, redirect to edit
            router.push(`/artists/${artistId}/reviews?edit=true`);
            return;
          }
          
          if (userReview) {
            setExistingReview(userReview.data);
          } else if (isEditMode) {
            // No review to edit, redirect to create
            router.push(`/artists/${artistId}/reviews`);
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
  }, [artistId, isAuthenticated, currentUser, isEditMode, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!artist) return;

    try {
      setSubmitLoading(true);

      if (isEditMode && existingReview) {
        // Update existing review
        await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      } else {
        // Create new review
        await dispatch(createArtistReviewAsync({ 
          artistId: artist.id, 
          reviewData: {
            title: data.title,
            description: data.description,
            rating: data.rating
          }
        })).unwrap();
      }
      
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
      await dispatch(deleteReviewAsync(existingReview.id)).unwrap();
      router.push(`/artists/${artistId}`);
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (!isAuthenticated) {
    return null; // Will redirect
  }

  if (loading || loadingArtist || !artist) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading...</div>
        </div>
      </Layout>
    );
  }

  const artistImgUrl = artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png';

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

