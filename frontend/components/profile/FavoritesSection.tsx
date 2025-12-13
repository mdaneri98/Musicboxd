import { useTranslation } from 'react-i18next';
import { ArtistCard, AlbumCard, SongCard } from '@/components/cards';
import { LoadingSpinner } from '@/components/ui';
import type { Artist, Album, Song } from '@/types';

interface FavoritesSectionProps {
  favoriteArtists: Artist[];
  favoriteAlbums: Album[];
  favoriteSongs: Song[];
  loading?: boolean;
}

export default function FavoritesSection({
  favoriteArtists,
  favoriteAlbums,
  favoriteSongs,
  loading,
}: FavoritesSectionProps) {
  const { t } = useTranslation();

  if (loading) {
    return (
      <div className="loading-container">
        <LoadingSpinner size="large" message={t('profile.loadingFavorites')} />
      </div>
    );
  }

  const hasNoFavorites = favoriteArtists.length === 0 &&
                         favoriteAlbums.length === 0 &&
                         favoriteSongs.length === 0;

  return (
    <section className="favorites-section">
      {hasNoFavorites && (
        <div className="empty-state">
          <p className="add-favorites">{t('common.noFavoritesYet')}</p>
        </div>
      )}

      {/* Favorite Artists */}
      {favoriteArtists.length > 0 && (
        <div>
          <h2>{t('profile.favoriteArtists')}</h2>
          <div className="carousel-container">
            <div className="carousel">
              {favoriteArtists.map((artist) => (
                <ArtistCard key={artist.id} artist={artist} />
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Favorite Albums */}
      {favoriteAlbums.length > 0 && (
        <div>
          <h2>{t('profile.favoriteAlbums')}</h2>
          <div className="carousel-container">
            <div className="carousel">
              {favoriteAlbums.map((album) => (
                <AlbumCard key={album.id} album={album} />
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Favorite Songs */}
      {favoriteSongs.length > 0 && (
        <div>
          <h2>{t('profile.favoriteSongs')}</h2>
          <ul className="song-list">
            {favoriteSongs.map((song, index) => (
              <SongCard key={song.id} song={song} index={index} />
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
