import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useAppDispatch } from '@/store/hooks';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, updateReviewAsync, createReviewAsync, fetchAlbumByIdAsync, selectCurrentAlbum, fetchAlbumReviewsAsync, deleteReviewAsync } from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { Album, ReviewFormData } from '@/types';

const AlbumReviewPage = () => {
  const router = useRouter();
  const { id, edit } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const album = useAppSelector(selectCurrentAlbum) as Album;
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
        const albumId = parseInt(id as string);
        await dispatch(fetchAlbumByIdAsync(albumId)).unwrap();
        // Check if user already reviewed this album
        if (currentUser) {
          const reviews = await dispatch(fetchAlbumReviewsAsync({ albumId, page: 0, size: 100 })).unwrap();
          const userReview = reviews.items.find((r: any) => r.data.user_id === currentUser.id);
          
          if (userReview && !isEditMode) {
            router.push(`/albums/${id}/reviews?edit=true`);
            return;
          }
          
          if (userReview) {
            setExistingReview(userReview);
          } else if (isEditMode) {
            router.push(`/albums/${id}/reviews`);
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
  }, [id, isAuthenticated, currentUser, isEditMode, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    try {
      setSubmitLoading(true);

      if (isEditMode && existingReview) {
        // Update existing review
        await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      } else {
        await dispatch(createReviewAsync(data)).unwrap();
      }
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
      router.push(`/albums/${id}`);
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (!isAuthenticated) {
    return null;
  }

  if (loading || !album) {
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
                  <p className="review-preview-subtitle">{album.release_date.toLocaleDateString()}</p>
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

