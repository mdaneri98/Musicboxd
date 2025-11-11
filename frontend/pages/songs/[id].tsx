import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { RatingCard } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { songRepository, albumRepository, artistRepository, imageRepository } from '@/repositories';
import type { Song, Album, Artist, Review } from '@/types';

const SongDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const [song, setSong] = useState<Song | null>(null);
  const [album, setAlbum] = useState<Album | null>(null);
  const [artist, setArtist] = useState<Artist | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | undefined>();
  const [reviewsPage, setReviewsPage] = useState(0);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();
  const [isReviewed, setIsReviewed] = useState(false);

  // Fetch song data
  useEffect(() => {
    const fetchSong = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        setError(undefined);
        const songId = parseInt(id as string);
        
        const songData = await songRepository.getSongById(songId);
        setSong(songData);
        setIsFavorite(songData.isFavorite || false);
        
        // Fetch album and artist data
        const albumData = await albumRepository.getAlbumById(songData.albumId);
        setAlbum(albumData);
        
        const artistData = await artistRepository.getArtistById(songData.artistId);
        setArtist(artistData);
      } catch (err: any) {
        console.error('Failed to fetch song:', err);
        setError(err.message || 'Failed to load song');
      } finally {
        setLoading(false);
      }
    };

    fetchSong();
  }, [id]);

  // Fetch reviews
  useEffect(() => {
    const fetchReviews = async () => {
      if (!id) return;
      
      try {
        const songId = parseInt(id as string);
        const reviewsData = await songRepository.getSongReviews(songId, reviewsPage, 20);
        setReviews(reviewsData.items);
        setHasMoreReviews(reviewsData.items.length === 20);
        
        // Check if current user has reviewed this song
        if (isAuthenticated && currentUser) {
          const userReview = reviewsData.items.find(r => r.userId === currentUser.id);
          if (userReview) {
            setIsReviewed(true);
            setUserRating(userReview.rating);
          }
        }
      } catch (err) {
        console.error('Failed to fetch reviews:', err);
      }
    };

    if (id) {
      fetchReviews();
    }
  }, [id, reviewsPage, isAuthenticated, currentUser]);

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!song) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await songRepository.removeSongFavorite(song.id);
        setIsFavorite(false);
      } else {
        await songRepository.addSongFavorite(song.id);
        setIsFavorite(true);
      }
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  const formatDate = (date?: Date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading || !song) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading song...</div>
        </div>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout title="Error">
        <div className="content-wrapper">
          <div className="alert alert-danger" role="alert">
            <strong>Error:</strong> {error}
          </div>
        </div>
      </Layout>
    );
  }

  const albumImgUrl = album?.imageId ? imageRepository.getImageUrl(album.imageId) : '/assets/default-album.png';
  const artistImgUrl = artist?.imageId ? imageRepository.getImageUrl(artist.imageId) : '/assets/default-artist.png';

  return (
    <Layout title={`Musicboxd - ${song.title}`}>
      <div className="content-wrapper">
        {/* Song Header */}
        <section className="entity-header">
          <div className="entity-main-info">
            <img src={albumImgUrl} alt={song.title} className="entity-image album-cover" />
            <div className="entity-details">
              <div className="entity-type">
                Song
                {currentUser?.isModerator && (
                  <Link href={`/mod/songs/${song.id}/edit`} className="edit-link">
                    <i className="fas fa-pencil-alt"></i>
                  </Link>
                )}
              </div>
              <h1 className="entity-title">{song.title}</h1>
              
              <div className="song-metadata">
                {/* Artist */}
                {artist && (
                  <div className="artists-list">
                    <Link href={`/artists/${artist.id}`} className="artist-link">
                      <img src={artistImgUrl} alt={artist.name} className="artist-thumbnail" />
                      <span className="artist-name">{artist.name}</span>
                    </Link>
                  </div>
                )}

                {/* Album */}
                {album && (
                  <div className="album-link-container">
                    <Link href={`/albums/${album.id}`} className="album-link">
                      <img src={albumImgUrl} alt={album.title} className="album-thumbnail" />
                      <span className="album-name">{album.title}</span>
                    </Link>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Rating Card */}
          <div className="rating-card-container">
            <RatingCard
              totalRatings={song.reviewsCount || 0}
              averageRating={song.averageRating || 0}
              userRating={userRating}
              reviewed={isReviewed}
              entityType="songs"
              entityId={song.id}
              entityLabel="song"
            />
          </div>
        </section>

        {/* Action Buttons */}
        <section className="entity-actions">
          {!isAuthenticated ? (
            <Link href="/login" className="btn btn-primary">
              Login to Add Favorite
            </Link>
          ) : (
            <button
              onClick={handleFavoriteToggle}
              disabled={favoriteLoading}
              className={`btn ${isFavorite ? 'btn-secondary' : 'btn-primary'}`}
            >
              {favoriteLoading
                ? 'Loading...'
                : isFavorite
                ? 'Remove from Favorites'
                : 'Add to Favorites'}
            </button>
          )}
        </section>

        {/* Song Details */}
        <section className="entity-section song-details">
          <div className="song-info-grid">
            <div className="song-info-item">
              <span className="info-label">Duration:</span>
              <span className="info-value">{formatDuration(song.duration)}</span>
            </div>
            {album?.genre && (
              <div className="song-info-item">
                <span className="info-label">Genre:</span>
                <span className="info-value">{album.genre}</span>
              </div>
            )}
            {album?.releaseDate && (
              <div className="song-info-item">
                <span className="info-label">Release Date:</span>
                <span className="info-value">{formatDate(album.releaseDate)}</span>
              </div>
            )}
          </div>
        </section>

        {/* Reviews Section */}
        {reviews.length > 0 && (
          <section className="entity-section">
            <h2>Reviews</h2>
            <div className="reviews-grid">
              {reviews.map((review) => (
                <ReviewCard key={review.id} review={review} />
              ))}
            </div>

            {/* Pagination */}
            <div className="pagination">
              {reviewsPage > 0 && (
                <button
                  onClick={() => setReviewsPage(reviewsPage - 1)}
                  className="btn btn-secondary"
                >
                  Previous Page
                </button>
              )}
              {hasMoreReviews && (
                <button
                  onClick={() => setReviewsPage(reviewsPage + 1)}
                  className="btn btn-secondary"
                >
                  Next Page
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

