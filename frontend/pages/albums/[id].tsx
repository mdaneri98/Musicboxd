import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { RatingCard } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { albumRepository, artistRepository, imageRepository } from '@/repositories';
import type { Album, Artist, Song, Review } from '@/types';

const AlbumDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const [album, setAlbum] = useState<Album | null>(null);
  const [artist, setArtist] = useState<Artist | null>(null);
  const [songs, setSongs] = useState<Song[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | undefined>();
  const [reviewsPage, setReviewsPage] = useState(0);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();
  const [isReviewed, setIsReviewed] = useState(false);

  // Fetch album data
  useEffect(() => {
    const fetchAlbum = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        setError(undefined);
        const albumId = parseInt(id as string);
        
        const albumData = await albumRepository.getAlbumById(albumId);
        setAlbum(albumData);
        setIsFavorite(albumData.isFavorite || false);
        
        // Fetch artist data
        const artistData = await artistRepository.getArtistById(albumData.artistId);
        setArtist(artistData);
      } catch (err: any) {
        console.error('Failed to fetch album:', err);
        setError(err.message || 'Failed to load album');
      } finally {
        setLoading(false);
      }
    };

    fetchAlbum();
  }, [id]);

  // Fetch songs
  useEffect(() => {
    const fetchSongs = async () => {
      if (!id) return;
      
      try {
        const albumId = parseInt(id as string);
        const songsData = await albumRepository.getAlbumSongs(albumId, 0, 100); // Get all songs
        setSongs(songsData.items);
      } catch (err) {
        console.error('Failed to fetch songs:', err);
      }
    };

    if (id) {
      fetchSongs();
    }
  }, [id]);

  // Fetch reviews
  useEffect(() => {
    const fetchReviews = async () => {
      if (!id) return;
      
      try {
        const albumId = parseInt(id as string);
        const reviewsData = await albumRepository.getAlbumReviews(albumId, reviewsPage, 20);
        setReviews(reviewsData.items);
        setHasMoreReviews(reviewsData.items.length === 20);
        
        // Check if current user has reviewed this album
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

    if (!album) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await albumRepository.removeAlbumFavorite(album.id);
        setIsFavorite(false);
      } else {
        await albumRepository.addAlbumFavorite(album.id);
        setIsFavorite(true);
      }
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  const formatDate = (date?: Date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading || !album) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading album...</div>
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

  const albumImgUrl = album.imageId ? imageRepository.getImageUrl(album.imageId) : '/assets/default-album.png';
  const artistImgUrl = artist?.imageId ? imageRepository.getImageUrl(artist.imageId) : '/assets/default-artist.png';

  return (
    <Layout title={`Musicboxd - ${album.title}`}>
      <div className="content-wrapper">
        {/* Album Header */}
        <section className="entity-header">
          <div className="entity-main-info">
            <img src={albumImgUrl} alt={album.title} className="entity-image album-cover" />
            <div className="entity-details">
              <div className="entity-type">
                Album
                {currentUser?.isModerator && (
                  <Link href={`/mod/albums/${album.id}/edit`} className="edit-link">
                    <i className="fas fa-pencil-alt"></i>
                  </Link>
                )}
              </div>
              <h1 className="entity-title">{album.title}</h1>
              
              <div className="album-metadata">
                {artist && (
                  <Link href={`/artists/${artist.id}`} className="artist-link">
                    <img src={artistImgUrl} alt={artist.name} className="artist-thumbnail" />
                    <span className="artist-name">{artist.name}</span>
                  </Link>
                )}
                <div className="album-info">
                  {album.genre && <span className="album-genre">{album.genre}</span>}
                  {album.genre && album.releaseDate && <span className="info-separator">&bull;</span>}
                  {album.releaseDate && <span className="album-date">{formatDate(album.releaseDate)}</span>}
                </div>
              </div>
            </div>
          </div>

          {/* Rating Card */}
          <div className="rating-card-container">
            <RatingCard
              totalRatings={album.reviewsCount || 0}
              averageRating={album.averageRating || 0}
              userRating={userRating}
              reviewed={isReviewed}
              entityType="albums"
              entityId={album.id}
              entityLabel="album"
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

        {/* Songs Section */}
        {songs.length > 0 && (
          <section className="entity-section">
            <h2>Songs</h2>
            <ul className="song-list">
              {songs.map((song, index) => (
                <li key={song.id}>
                  <Link href={`/songs/${song.id}`} className="song-item">
                    <span className="song-number">{index + 1}</span>
                    <span className="song-title">{song.title}</span>
                    {song.averageRating !== undefined && (
                      <div className="rating-badge">
                        <span className="rating">{song.averageRating.toFixed(1)}</span>
                        <span className="star">&#9733;</span>
                      </div>
                    )}
                  </Link>
                </li>
              ))}
            </ul>
          </section>
        )}

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

export default AlbumDetailPage;

