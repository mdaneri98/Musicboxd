import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout, SongInfo } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser,
  fetchSongByIdAsync,
  fetchAlbumByIdAsync,
  fetchArtistByIdAsync,
  fetchSongReviewsAsync,
  addSongFavoriteAsync,
  removeSongFavoriteAsync,
  selectCurrentSong,
  selectSongReviews,
  selectLoadingSong,
  selectSongError,
  clearCurrentSong
} from '@/store/slices';
import type { Album, Artist, Review } from '@/types';
import { formatDate } from '@/utils/timeUtils';

const SongDetailPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { songId } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const song = useAppSelector(selectCurrentSong);
  const reviews = useAppSelector(selectSongReviews);
  const loading = useAppSelector(selectLoadingSong);
  const error = useAppSelector(selectSongError);
  
  const [album, setAlbum] = useState<Album | null>(null);
  const [artist, setArtist] = useState<Artist | null>(null);
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();

  useEffect(() => {
    return () => {
      dispatch(clearCurrentSong());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!songId) return;
    
    const songIdNum = parseInt(songId as string);
    dispatch(fetchSongByIdAsync(songIdNum))
      .unwrap()
      .then((songData) => {
        dispatch(fetchAlbumByIdAsync(songData.data.album_id))
          .unwrap()
          .then((albumData) => setAlbum(albumData.data))
          .catch((err) => console.error(t('common.error'), err));
        
        dispatch(fetchArtistByIdAsync(songData.data.artist_id))
          .unwrap()
          .then((artistData) => setArtist(artistData.data))
          .catch((err) => console.error(t('common.error'), err));

          if(songData.data.reviewed && isAuthenticated && currentUser) {
            const userReview = reviews.find((r: Review) => r.user_id === currentUser.id);
            if (userReview) {
              setUserRating(userReview.rating);
            }
          }
      })
      .catch((err) => console.error(t('common.error'), err));
  }, [songId, dispatch]);

  useEffect(() => {
    if (!songId) return;
    
    const songIdNum = parseInt(songId as string);
    dispatch(fetchSongReviewsAsync({ songId: songIdNum, page: reviewsPage, size: 20 }))
      .unwrap()
      .then((reviewsData) => {
        setHasMoreReviews(reviewsData.items.length === 20);
      })
      .catch((err) => console.error(t('common.error'), err));
  }, [songId, reviewsPage, isAuthenticated, currentUser, dispatch]);

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!song) return;

    try {
      setFavoriteLoading(true);
      if (song.favorite) {
        await dispatch(removeSongFavoriteAsync(song.id)).unwrap();
      } else {
        await dispatch(addSongFavoriteAsync(song.id)).unwrap();
      }
    } catch (err) {
      console.error(t('common.error'), err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };


  if (loading || !song) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <div className="loading">{t('song.loadingSong')}</div>
        </div>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout title={t('common.error')}>
        <div className="content-wrapper">
          <div className="alert alert-danger" role="alert">
            <strong>{t('common.error')}:</strong> {error}
          </div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - ${song.title}`}>
      <div className="content-wrapper">
        <SongInfo
          song={song}
          album={album}
          artist={artist}
          currentUser={currentUser}
          isAuthenticated={isAuthenticated}
          isFavorite={song.favorite || false}
          favoriteLoading={favoriteLoading}
          userRating={userRating}
          isReviewed={song.reviewed || false}
          onFavoriteToggle={handleFavoriteToggle}
        />

        {/* Song Details */}
        <section className="entity-section song-details">
          <div className="song-info-grid">
            <div className="song-info-item">
              <span className="info-label">{t('song.duration')}:</span>
              <span className="info-value">{formatDuration(parseInt(song.duration))}</span>
            </div>
            {album?.genre && (
              <div className="song-info-item">
                <span className="info-label">{t('song.genre')}:</span>
                <span className="info-value">{album.genre}</span>
              </div>
            )}
            {album?.release_date && (
              <div className="song-info-item">
                <span className="info-label">{t('song.releaseDate')}:</span>
                <span className="info-value">{formatDate(song.release_date)}</span>
              </div>
            )}
          </div>
        </section>

        {/* Reviews Section */}
        {reviews.length > 0 && (
          <section className="entity-section">
            <h2>{t('profile.reviews')}</h2>
            <div className="reviews-grid">
              {reviews.map((review) => (
                <ReviewCard key={review.id} review={review} />
              ))}
            </div>

            {/* Pagination */}
            <div className="pagination">
              {reviewsPage > 1 && (
                <button
                  onClick={() => setReviewsPage(reviewsPage - 1)}
                  className="btn btn-secondary"
                >
                  {t('song.previousPage')}
                </button>
              )}
              {hasMoreReviews && (
                <button
                  onClick={() => setReviewsPage(reviewsPage + 1)}
                  className="btn btn-secondary"
                >
                  {t('song.nextPage')}
                </button>
              )}
            </div>
          </section>
        )}
      </div>
    </Layout>
  );
};

export default SongDetailPage;

