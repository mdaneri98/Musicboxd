import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  createAlbumReviewAsync,
  fetchAlbumByIdAsync,
  selectCurrentAlbum,
  selectLoadingAlbum,
  fetchAlbumReviewsAsync,
  clearCurrentAlbum
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { ReviewFormData } from '@/types';
import { ReviewItemType } from '@/types/enums';
import { formatDate } from '@/utils/timeUtils';
import { LoadingSpinner } from '@/components/ui';
import { ASSETS } from '@/utils';
const AlbumReviewPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { albumId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const album = useAppSelector(selectCurrentAlbum);
  const loadingAlbum = useAppSelector(selectLoadingAlbum);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);

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
          const userReview = reviews.items.find((r: any) => r.user_id === currentUser.id);

          if (userReview) {
            router.push(`/albums/${albumId}/edit-review`);
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
  }, [albumId, isAuthenticated, currentUser, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!album) return;

    try {
      setSubmitLoading(true);
      await dispatch(createAlbumReviewAsync({
        title: data.title,
        description: data.description,
        rating: data.rating,
        item_id: album.id,
        item_type: ReviewItemType.ALBUM,
      })).unwrap();
      router.push(`/albums/${album.id}`);
    } catch (error) {
      console.error('Failed to submit review:', error);
    } finally {
      setSubmitLoading(false);
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
    <Layout title={`Musicboxd - ${t('album.reviewAlbum')} ${album.title}`}>
      <div className="content-wrapper">
        <h1 className="page-title">{t('album.makeReview')}</h1>

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
          />
        </div>
      </div>
    </Layout>
  );
};

export default AlbumReviewPage;
