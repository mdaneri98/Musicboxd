import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  fetchSongByIdAsync,
  fetchAlbumByIdAsync,
  fetchSongReviewsAsync,
  createSongReviewAsync,
  selectCurrentSong,
  selectLoadingSong,
  clearCurrentSong
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { Album, ReviewFormData, HALResource, Review } from '@/types';
import { ReviewItemType } from '@/types/enums';
import { LoadingSpinner } from '@/components/ui';

const SongReviewPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { songId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const song = useAppSelector(selectCurrentSong);
  const loadingSong = useAppSelector(selectLoadingSong);
  const [album, setAlbum] = useState<Album | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);

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

        const albumData = await dispatch(fetchAlbumByIdAsync(fetchedSong.data.album_id)).unwrap();
        setAlbum(albumData.data);

        if (currentUser) {
          const reviews = await dispatch(fetchSongReviewsAsync({ songId: songIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find((r: HALResource<Review>) => r.data.user_id === currentUser.id);

          if (userReview) {
            router.push(`/songs/${songId}/edit-review`);
            return;
          }
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
    if (!song) return;

    try {
      setSubmitLoading(true);
      await dispatch(createSongReviewAsync({
        title: data.title,
        description: data.description,
        rating: data.rating,
        item_id: song.id,
        item_type: ReviewItemType.SONG,
      })).unwrap();
      router.push(`/songs/${song.id}`);
    } catch (error) {
      console.error('Failed to submit review:', error);
    } finally {
      setSubmitLoading(false);
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

  const albumImgUrl = album?.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/image-placeholder.png';

  return (
    <Layout title={`Musicboxd - ${t('song.reviewSong')} ${song.title}`}>
      <div className="content-wrapper">
        <h1 className="page-title">{t('song.makeReview')}</h1>

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
          />
        </div>
      </div>
    </Layout>
  );
};

export default SongReviewPage;
