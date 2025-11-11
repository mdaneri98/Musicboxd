import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { albumRepository, reviewRepository, imageRepository } from '@/repositories';
import type { Album, ReviewFormData } from '@/types';

const AlbumReviewPage = () => {
  const router = useRouter();
  const { id, edit } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [album, setAlbum] = useState<Album | null>(null);
  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const isEditMode = edit === 'true';

  const formatDate = (date?: Date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

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
        const albumData = await albumRepository.getAlbumById(albumId);
        setAlbum(albumData);

        // Check if user already reviewed this album
        if (currentUser) {
          const reviews = await albumRepository.getAlbumReviews(albumId, 0, 100);
          const userReview = reviews.items.find(r => r.userId === currentUser.id);
          
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
  }, [id, isAuthenticated, currentUser, isEditMode, router]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!album) return;

    try {
      setSubmitLoading(true);

      if (isEditMode && existingReview) {
        await reviewRepository.updateReview(existingReview.id, {
          title: data.title,
          description: data.description,
          rating: data.rating,
        });
      } else {
        await albumRepository.createAlbumReview(album.id, {
          title: data.title,
          description: data.description,
          rating: data.rating,
        });
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
      await reviewRepository.deleteReview(existingReview.id);
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

  const albumImgUrl = album.imageId ? imageRepository.getImageUrl(album.imageId) : '/assets/default-album.png';

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
              {album.releaseDate && (
                <p className="review-preview-subtitle">{formatDate(album.releaseDate)}</p>
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

