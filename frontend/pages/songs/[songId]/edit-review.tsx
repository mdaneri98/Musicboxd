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
  fetchSongByIdAsync,
  fetchAlbumByIdAsync,
  fetchSongReviewsAsync,
  updateReviewAsync,
  deleteReviewAsync,
  selectCurrentSong,
  selectLoadingSong,
  clearCurrentSong
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { Album, ReviewFormData, HALResource, Review } from '@/types';
import { ReviewItemType } from '@/types/enums';
import { ASSETS } from '@/utils';
const EditSongReviewPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { songId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const song = useAppSelector(selectCurrentSong);
  const loadingSong = useAppSelector(selectLoadingSong);
  const [album, setAlbum] = useState<Album | null>(null);
  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentSong());
    };
  }, [dispatch]);

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      if (!songId) return;

      try {
        setLoading(true);
        const songIdNum = parseInt(songId as string);
        const fetchedSong = await dispatch(fetchSongByIdAsync(songIdNum)).unwrap();

        const albumData = await dispatch(fetchAlbumByIdAsync(fetchedSong.album_id)).unwrap();
        setAlbum(albumData);

        if (currentUser) {
          const reviews = await dispatch(fetchSongReviewsAsync({ songId: songIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find((r: HALResource<Review>) => r.user_id === currentUser.id);

          if (!userReview) {
            // No review to edit, redirect to create
            router.push(`/songs/${songId}/reviews`);
            return;
          }

          setExistingReview(userReview);
        }
      } catch (error) {
        console.error('Failed to fetch song:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [songId, isAuthenticated, currentUser, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!song || !existingReview) return;

    try {
      setSubmitLoading(true);
      data.item_type = ReviewItemType.SONG;
      await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      router.push(`/songs/${song.id}`);
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
      router.push(`/songs/${songId}`);
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (!isAuthenticated) {
    return null;
  }

  if (loading || loadingSong || !song) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
        </div>
      </Layout>
    );
  }

  const albumImgUrl = album?.image_id ? imageRepository.getImageUrl(album.image_id) : ASSETS.IMAGE_PLACEHOLDER;

  return (
    <Layout title={`Musicboxd - ${t('song.editReviewFor')} ${song.title}`}>
      <div className="content-wrapper">
        <h1 className="page-title">{t('song.editYourReview')}</h1>

        {/* Song Preview */}
        <div className="review-preview">
          <Link href={`/songs/${song.id}`} className="review-preview-link">
            <img src={albumImgUrl} alt={song.title} className="review-preview-image" />
            <div className="review-preview-info">
              <h2 className="review-preview-title">{song.title}</h2>
              <p className="review-preview-subtitle">{formatDuration(parseInt(song.duration))}</p>
            </div>
          </Link>
        </div>

        {/* Review Form */}
        <div className="review-section">
          <ReviewForm
            onSubmit={handleSubmit}
            onCancel={() => router.push(`/songs/${song.id}`)}
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
        message={t('song.confirmDeleteReview')}
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteModal(false)}
        confirmText={t('song.yes')}
        cancelText={t('song.no')}
      />
    </Layout>
  );
};

export default EditSongReviewPage;
