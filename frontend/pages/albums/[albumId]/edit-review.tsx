import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { useAppDispatch } from '@/store/hooks';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal, LoadingSpinner } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  updateReviewAsync, 
  fetchAlbumByIdAsync, 
  selectCurrentAlbum, 
  selectLoadingAlbum,
  fetchAlbumReviewsAsync, 
  deleteReviewAsync,
  clearCurrentAlbum
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { ReviewFormData } from '@/types';
import { formatDate } from '@/utils/timeUtils';
import { ReviewItemType } from '@/types/enums';
import { ASSETS } from '@/utils';

const EditAlbumReviewPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { albumId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const album = useAppSelector(selectCurrentAlbum);
  const loadingAlbum = useAppSelector(selectLoadingAlbum);
  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

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

        if (currentUser) {
          const reviews = await dispatch(fetchAlbumReviewsAsync({ albumId: albumIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find((r: any) => r.data.user_id === currentUser.id);
          
          if (!userReview) {
            // No review to edit, redirect to create
            router.push(`/albums/${albumId}/reviews`);
            return;
          }
          
          setExistingReview(userReview.data);
        }
      } catch (error) {
        console.error('Failed to fetch album:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [albumId, isAuthenticated, currentUser, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!album || !existingReview) return;
    
    try {
      setSubmitLoading(true);
      data.item_type = ReviewItemType.ALBUM;
      await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      router.push(`/albums/${album.id}`);
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
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
        </div>
      </Layout>
    );
  }

  const albumImgUrl = album.image_id ? imageRepository.getImageUrl(album.image_id) : ASSETS.IMAGE_PLACEHOLDER;

  return (
    <Layout title={`Musicboxd - ${t('album.editReviewFor')} ${album.title}`}>
      <div className="content-wrapper">
        <h1 className="page-title">{t('album.editYourReview')}</h1>

        {/* Album Preview */}
        <div className="review-preview">
          <Link href={`/albums/${album.id}`} className="review-preview-link">
            <img src={albumImgUrl} alt={album.title} className="review-preview-image" />
            <div className="review-preview-info">
              <h2 className="review-preview-title">{album.title}</h2>
              {album.release_date && (
                  <p className="review-preview-subtitle">{formatDate(album.release_date)}</p>
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
        message={t('album.confirmDeleteReview')}
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteModal(false)}
        confirmText={t('album.yes')}
        cancelText={t('album.no')}
      />
    </Layout>
  );
};

export default EditAlbumReviewPage;

