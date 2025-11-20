import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useAppDispatch } from '@/store/hooks';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  updateReviewAsync, 
  createAlbumReviewAsync, 
  fetchAlbumByIdAsync, 
  selectCurrentAlbum, 
  selectLoadingAlbum,
  fetchAlbumReviewsAsync, 
  deleteReviewAsync,
  clearCurrentAlbum
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { ReviewFormData } from '@/types';

const AlbumReviewPage = () => {
  const router = useRouter();
  const { albumId, edit } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const album = useAppSelector(selectCurrentAlbum);
  const loadingAlbum = useAppSelector(selectLoadingAlbum);
  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const isEditMode = edit === 'true';

  // Limpiar al desmontar
  useEffect(() => {
    return () => {
      dispatch(clearCurrentAlbum());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      if (!albumId) return;

      try {
        setLoading(true);
        const albumIdNum = parseInt(albumId as string);
        await dispatch(fetchAlbumByIdAsync(albumIdNum)).unwrap();
        // Check if user already reviewed this album
        if (currentUser) {
          const reviews = await dispatch(fetchAlbumReviewsAsync({ albumId: albumIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find((r: any) => r.data.user_id === currentUser.id);
          
          if (userReview && !isEditMode) {
            router.push(`/albums/${albumId}/reviews?edit=true`);
            return;
          }
          
          if (userReview) {
            setExistingReview(userReview.data);
          } else if (isEditMode) {
            router.push(`/albums/${albumId}/reviews`);
            return;
          }
        }
      } catch (error) {
        console.error('Failed to fetch album:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [albumId, isAuthenticated, currentUser, isEditMode, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!album) return;
    
    try {
      setSubmitLoading(true);

      if (isEditMode && existingReview) {
        // Update existing review
        await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      } else {
        // Create new review
        await dispatch(createAlbumReviewAsync({ 
          albumId: album.id, 
          reviewData: {
            title: data.title,
            description: data.description,
            rating: data.rating
          }
        })).unwrap();
      }
      
      router.push(`/albums/${album.id}`);
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
      router.push(`/albums/${albumId}`);
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (!isAuthenticated) {
    return null;
  }

  if (loading || loadingAlbum || !album) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading...</div>
        </div>
      </Layout>
    );
  }

  const albumImgUrl = album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png';

  return (
    <Layout title={`Musicboxd - Review ${album.title}`}>
      <div className="content-wrapper">
        <h1 className="page-title">
          {isEditMode ? 'Edit Your Review' : 'Make a Review'}
        </h1>

        {/* Album Preview */}
        <div className="review-preview">
          <Link href={`/albums/${album.id}`} className="review-preview-link">
            <img src={albumImgUrl} alt={album.title} className="review-preview-image" />
            <div className="review-preview-info">
              <h2 className="review-preview-title">{album.title}</h2>
              {album.release_date && (
<<<<<<< HEAD:frontend/pages/albums/[id]/reviews.tsx
                  <p className="review-preview-subtitle">{album.release_date.toString()}</p>
=======
                  <p className="review-preview-subtitle">{new Date(album.release_date).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</p>
>>>>>>> 1eec8ecc86241f8a906cfd3d0cd3ebbe658a87bd:frontend/pages/albums/[albumId]/reviews.tsx
              )}
            </div>
          </Link>
        </div>

        {/* Review Form */}
        <div className="review-section">
          <ReviewForm
            onSubmit={handleSubmit}
            onCancel={() => router.push(`/albums/${album.id}`)}
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

export default AlbumReviewPage;

