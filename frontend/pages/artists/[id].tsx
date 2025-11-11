import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewCard } from '@/components/cards';
import { RatingCard } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { artistRepository, imageRepository } from '@/repositories';
import type { Artist, Album, Song, Review, HALResource } from '@/types';

const ArtistDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  
  const [artist, setArtist] = useState<Artist | null>(null);
  const [albums, setAlbums] = useState<Album[]>([]);
  const [songs, setSongs] = useState<Song[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | undefined>();
  const [reviewsPage, setReviewsPage] = useState(1);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [userRating, setUserRating] = useState<number | undefined>();
  const [isReviewed, setIsReviewed] = useState(false);

  // Fetch artist data
  useEffect(() => {
    const fetchArtist = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        setError(undefined);
        const artistId = parseInt(id as string);
        
        const artistData = await artistRepository.getArtistById(artistId);
        setArtist(artistData.data as Artist);
        setIsFavorite(artistData.data.is_favorite || false);
      } catch (err: any) {
        console.error('Failed to fetch artist:', err);
        setError(err.message || 'Failed to load artist');
      } finally {
        setLoading(false);
      }
    };

    fetchArtist();
  }, [id]);

  // Fetch albums and songs
  useEffect(() => {
    const fetchMusicData = async () => {
      if (!id) return;
      
      try {
        const artistId = parseInt(id as string);
        const [albumsData, songsData] = await Promise.all([
          artistRepository.getArtistAlbums(artistId, 0, 10),
          artistRepository.getArtistSongs(artistId, 0, 10),
        ]);
        
        setAlbums(albumsData.items.map((item: HALResource<Album>) => item.data as Album));
        setSongs(songsData.items.map((item: HALResource<Song>) => item.data as Song));
      } catch (err) {
        console.error('Failed to fetch music data:', err);
      }
    };

    if (id) {
      fetchMusicData();
    }
  }, [id]);

  // Fetch reviews
  useEffect(() => {
    const fetchReviews = async () => {
      if (!id) return;
      
      try {
        const artistId = parseInt(id as string);
        const reviewsData = await artistRepository.getArtistReviews(artistId, reviewsPage, 20);
        setReviews(reviewsData.items.map((item: HALResource<Review>) => item.data as Review));
        setHasMoreReviews(reviewsData.items.length === 20);
        
        // Check if current user has reviewed this artist
        if (isAuthenticated && currentUser) {
          const userReview = reviewsData.items.find((r: HALResource<Review>) => r.data.user_id === currentUser.id);
          if (userReview) {
            setIsReviewed(true);
            setUserRating(userReview.data.rating || 0);
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

    if (!artist) return;

    try {
      setFavoriteLoading(true);
      if (isFavorite) {
        await artistRepository.removeArtistFavorite(artist.id);
        setIsFavorite(false);
      } else {
        await artistRepository.addArtistFavorite(artist.id);
        setIsFavorite(true);
      }
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    } finally {
      setFavoriteLoading(false);
    }
  };

  if (loading || !artist) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading artist...</div>
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

  const artistImgUrl = artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png';

  return (
    <Layout title={`Musicboxd - ${artist.name}`}>
      <div className="content-wrapper">
        {/* Artist Header */}
        <section className="entity-header">
          <div className="entity-main-info">
            <img src={artistImgUrl} alt={artist.name} className="entity-image" />
            <div className="entity-details">
              <div className="entity-type">
                Artist
                {currentUser?.is_moderator && (
                  <Link href={`/mod/artists/${artist.id}/edit`} className="edit-link">
                    <i className="fas fa-pencil-alt"></i>
                  </Link>
                )}
              </div>
              <h1 className="entity-title">{artist.name}</h1>
              {artist.bio && <p className="entity-description">{artist.bio}</p>}
            </div>
          </div>

          {/* Rating Card */}
          <div className="rating-card-container">
            <RatingCard
              totalRatings={artist.rating_count || 0}
              averageRating={artist.avg_rating || 0}
              userRating={userRating}
              reviewed={isReviewed}
              entityType="artists"
              entityId={artist.id}
              entityLabel="artist"
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

        {/* Albums Section */}
        {albums.length > 0 && (
          <section className="entity-section">
            <h2>Albums</h2>
            <div className="carousel-container">
              <div className="carousel">
                {albums.map((album) => (
                  <div key={album.id} className="music-item">
                    <Link href={`/albums/${album.id}`} className="music-item-link">
                      <div className="music-item-image-container">
                        <img
                          src={album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png'}
                          alt={album.title}
                          className="music-item-image"
                        />
                        {album.avg_rating && (
                          <div className="rating-badge">
                            <span className="rating">{album.avg_rating.toFixed(1)}</span>
                            <span className="star">&#9733;</span>
                          </div>
                        )}
                      </div>
                      <p className="music-item-title">{album.title}</p>
                    </Link>
                  </div>
                ))}
              </div>
            </div>
          </section>
        )}

        {/* Songs Section */}
        {songs.length > 0 && (
          <section className="entity-section">
            <h2>Popular Songs</h2>
            <ul className="song-list">
              {songs.map((song, index) => (
                <li key={song.id}>
                  <Link href={`/songs/${song.id}`} className="song-item">
                    <span className="song-number">{index + 1}</span>
                    <span className="song-title">{song.title}</span>
                    {song.avg_rating && (
                      <div className="rating-badge">
                        <span className="rating">{song.avg_rating.toFixed(1)}</span>
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
              {reviewsPage > 1 && (
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

export default ArtistDetailPage;

