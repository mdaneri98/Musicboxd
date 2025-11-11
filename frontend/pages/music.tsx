import { useEffect, useState } from 'react';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { artistRepository, albumRepository, songRepository, imageRepository } from '@/repositories';
import { Artist, Album, Song, FilterTypeEnum } from '@/types';

type Tab = 'popular' | 'topRated';

const MusicDiscoveryPage = () => {
  const [artistTab, setArtistTab] = useState<Tab>('popular');
  const [albumTab, setAlbumTab] = useState<Tab>('popular');
  const [songTab, setSongTab] = useState<Tab>('popular');
  
  const [topRatedArtists, setTopRatedArtists] = useState<Artist[]>([]);
  const [popularArtists, setPopularArtists] = useState<Artist[]>([]);
  const [topRatedAlbums, setTopRatedAlbums] = useState<Album[]>([]);
  const [popularAlbums, setPopularAlbums] = useState<Album[]>([]);
  const [topRatedSongs, setTopRatedSongs] = useState<Song[]>([]);
  const [popularSongs, setPopularSongs] = useState<Song[]>([]);
  
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDiscoveryContent = async () => {
      try {
        setLoading(true);
        
        // Fetch artists
        const [topArtists, popArtists] = await Promise.all([
          artistRepository.getArtists(1, 10, undefined, FilterTypeEnum.RATING),
          artistRepository.getArtists(1, 10, undefined, FilterTypeEnum.POPULAR),
        ]);
        setTopRatedArtists(topArtists.items.map((item) => item.data));
        setPopularArtists(popArtists.items.map((item) => item.data));
        
        // Fetch albums
        const [topAlbums, popAlbums] = await Promise.all([
          albumRepository.getAlbums(1, 10, undefined, FilterTypeEnum.RATING),
          albumRepository.getAlbums(1, 10, undefined, FilterTypeEnum.POPULAR),
        ]);
        setTopRatedAlbums(topAlbums.items.map((item) => item.data));
        setPopularAlbums(popAlbums.items.map((item) => item.data));
        
        // Fetch songs
        const [topSongs, popSongs] = await Promise.all([
          songRepository.getSongs(1, 10, undefined, FilterTypeEnum.RATING),
          songRepository.getSongs(1, 10, undefined, FilterTypeEnum.POPULAR),
        ]);
        setTopRatedSongs(topSongs.items.map((item) => item.data));
        setPopularSongs(popSongs.items.map((item) => item.data));
      } catch (error) {
        console.error('Failed to fetch discovery content:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDiscoveryContent();
  }, []);

  return (
    <Layout title="Musicboxd - Music Discovery">
      <div className="content-wrapper">
        <div className="discovery-header">
          <div className="call-to-action-container">
            <h1>Discovery</h1>
            <h4>Explore the best music, curated for you</h4>
          </div>
        </div>

        {loading ? (
          <div className="loading">Loading music...</div>
        ) : (
          <>
            {/* Artists Section */}
            <section className="discovery-section">
              <div className="section-header">
                <div className="tabs">
                  <span
                    id="popularArtistButton"
                    className={`tab ${artistTab === 'popular' ? 'active' : ''}`}
                    onClick={() => setArtistTab('popular')}
                    style={{ cursor: 'pointer' }}
                  >
                    Most Popular
                  </span>
                  <span
                    id="topRatedArtistButton"
                    className={`tab ${artistTab === 'topRated' ? 'active' : ''}`}
                    onClick={() => setArtistTab('topRated')}
                    style={{ cursor: 'pointer' }}
                  >
                    Top Rated
                  </span>
                </div>
              </div>

              {artistTab === 'topRated' && topRatedArtists.length > 0 && (
                <div id="topRatedArtistTab" className="tab-content">
                  <h2>
                    Top Rated Artists
                    <Link href="/music/view-all?tab=artists" className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {topRatedArtists.map((artist) => (
                        <div key={artist.id} className="music-item artist-item">
                          <Link href={`/artists/${artist.id}`} className="music-item-link">
                            <div className="music-item-image-container">
                              <img
                                src={artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png'}
                                alt={artist.name}
                                className="music-item-image"
                              />
                              {artist.avg_rating !== 0 && (
                                <div className="rating-badge">
                                  <span className="rating">{artist.avg_rating.toFixed(1)}</span>
                                  <span className="star">&#9733;</span>
                                </div>
                              )}
                            </div>
                            <p className="music-item-title">{artist.name}</p>
                          </Link>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {artistTab === 'popular' && popularArtists.length > 0 && (
                <div id="popularArtistTab" className="tab-content">
                  <h2>
                    Most Popular Artists
                    <Link href="/music/view-all?tab=artists" className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {popularArtists.map((artist) => (
                        <div key={artist.id} className="music-item artist-item">
                          <Link href={`/artists/${artist.id}`} className="music-item-link">
                            <div className="music-item-image-container">
                              <img
                                src={artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '/assets/default-artist.png'}
                                alt={artist.name}
                                className="music-item-image"
                              />
                              {artist.avg_rating !== 0 && (
                                <div className="rating-badge">
                                  <span className="rating">{artist.avg_rating.toFixed(1)}</span>
                                  <span className="star">&#9733;</span>
                                </div>
                              )}
                            </div>
                            <p className="music-item-title">{artist.name}</p>
                          </Link>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </section>

            {/* Albums Section */}
            <section className="discovery-section">
              <div className="section-header">
                <div className="tabs">
                  <span
                    id="popularAlbumButton"
                    className={`tab ${albumTab === 'popular' ? 'active' : ''}`}
                    onClick={() => setAlbumTab('popular')}
                    style={{ cursor: 'pointer' }}
                  >
                    Most Popular
                  </span>
                  <span
                    id="topRatedAlbumButton"
                    className={`tab ${albumTab === 'topRated' ? 'active' : ''}`}
                    onClick={() => setAlbumTab('topRated')}
                    style={{ cursor: 'pointer' }}
                  >
                    Top Rated
                  </span>
                </div>
              </div>

              {albumTab === 'topRated' && topRatedAlbums.length > 0 && (
                <div id="topRatedAlbumTab" className="tab-content">
                  <h2>
                    Top Rated Albums
                    <Link href="/music/view-all?tab=albums" className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {topRatedAlbums.map((album) => (
                        <div key={album.id} className="music-item album-item">
                          <Link href={`/albums/${album.id}`} className="music-item-link">
                            <div className="music-item-image-container">
                              <img
                                src={album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png'}
                                alt={album.title}
                                className="music-item-image"
                              />
                              {album.avg_rating !== 0 && (
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
                </div>
              )}

              {albumTab === 'popular' && popularAlbums.length > 0 && (
                <div id="popularAlbumTab" className="tab-content">
                  <h2>
                    Most Popular Albums
                    <Link href="/music/view-all?tab=albums" className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {popularAlbums.map((album) => (
                        <div key={album.id} className="music-item album-item">
                          <Link href={`/albums/${album.id}`} className="music-item-link">
                            <div className="music-item-image-container">
                              <img
                                src={album.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png'}
                                alt={album.title}
                                className="music-item-image"
                              />
                                {album.avg_rating !== 0 && (
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
                </div>
              )}
            </section>

            {/* Songs Section */}
            <section className="discovery-section">
              <div className="section-header">
                <div className="tabs">
                  <span
                    id="popularSongButton"
                    className={`tab ${songTab === 'popular' ? 'active' : ''}`}
                    onClick={() => setSongTab('popular')}
                    style={{ cursor: 'pointer' }}
                  >
                    Most Popular
                  </span>
                  <span
                    id="topRatedSongButton"
                    className={`tab ${songTab === 'topRated' ? 'active' : ''}`}
                    onClick={() => setSongTab('topRated')}
                    style={{ cursor: 'pointer' }}
                  >
                    Top Rated
                  </span>
                </div>
              </div>

              {songTab === 'topRated' && topRatedSongs.length > 0 && (
                <div id="topRatedSongTab" className="tab-content">
                  <h2>
                    Top Rated Songs
                    <Link href="/music/view-all?tab=songs" className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <ul className="song-list">
                    {topRatedSongs.map((song, index) => (
                      <li key={song.id}>
                        <Link href={`/songs/${song.id}`} className="song-item">
                          <span className="song-number">{index + 1}</span>
                          <span className="song-title">{song.title}</span>
                          {song.avg_rating !== 0 && (
                            <div className="rating-badge">
                              <span className="rating">{song.avg_rating.toFixed(1)}</span>
                              <span className="star">&#9733;</span>
                            </div>
                          )}
                        </Link>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {songTab === 'popular' && popularSongs.length > 0 && (
                <div id="popularSongTab" className="tab-content">
                  <h2>
                    Most Popular Songs
                    <Link href="/music/view-all?tab=songs" className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <ul className="song-list">
                    {popularSongs.map((song, index) => (
                      <li key={song.id}>
                        <Link href={`/songs/${song.id}`} className="song-item">
                          <span className="song-number">{index + 1}</span>
                          <span className="song-title">{song.title}</span>
                            {song.avg_rating !== 0 && (
                            <div className="rating-badge">
                              <span className="rating">{song.avg_rating.toFixed(1)}</span>
                              <span className="star">&#9733;</span>
                            </div>
                          )}
                        </Link>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </section>
          </>
        )}
      </div>
    </Layout>
  );
};

export default MusicDiscoveryPage;

