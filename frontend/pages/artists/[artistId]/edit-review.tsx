import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal, LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  fetchArtistByIdAsync, 
  fetchArtistReviewsAsync, 
  updateReviewAsync, 
  deleteReviewAsync, 
  selectCurrentArtist,
  selectLoadingArtist,
  clearCurrentArtist
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { ReviewFormData } from '@/types';
import { ReviewItemType } from '@/types/enums';

const EditArtistReviewPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { artistId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const artist = useAppSelector(selectCurrentArtist);
  const loadingArtist = useAppSelector(selectLoadingArtist);

  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

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

        if (currentUser) {
          const reviews = await dispatch(fetchArtistReviewsAsync({ artistId: artistIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find(r => r.data.user_id === currentUser.id);
          
          if (!userReview) {
            // No review to edit, redirect to create
            router.push(`/artists/${artistId}/reviews`);
            return;
          }
          
          setExistingReview(userReview.data);
        }
      } catch (error) {
        console.error('Failed to fetch artist:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [artistId, isAuthenticated, currentUser, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!artist || !existingReview) return;

    try {
      setSubmitLoading(true);
      data.item_type = ReviewItemType.ARTIST;
      await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      router.push(`/artists/${artist.id}`);
    } catch (error) {
      console.error('Failed to update review:', error);
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
    return null;
  }

  if (loading || loadingArtist || !artist) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
        </div>
      </Layout>
    );
  }

  const artistImgUrl = artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/image-placeholder.png';

  return (
    <Layout title={`Musicboxd - ${t('artist.editReviewFor')} ${artist.name}`}>
      <div className="content-wrapper">
        <h1 className="page-title">{t('artist.editYourReview')}</h1>

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

          {existingReview && (
            <div className="delete-review-container">
              <button
                type="button"
                onClick={() => setShowDeleteModal(true)}
                className="btn btn-danger"
              >
                {t('review.deleteReview')}
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        message={t('artist.confirmDeleteReview')}
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteModal(false)}
        confirmText={t('artist.yes')}
        cancelText={t('artist.no')}
      />
    </Layout>
  );
};

export default EditArtistReviewPage;

