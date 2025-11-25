import { useEffect, useState } from 'react';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchArtistsAsync, fetchAlbumsAsync, fetchSongsAsync, selectArtistLoading, selectAlbumLoading, selectSongLoading } from '@/store/slices';
import { Artist, Album, Song, FilterTypeEnum, MusicTabEnum, ReviewItemTypeEnum } from '@/types';
import ArtistCard from '@/components/cards/ArtistCard';
import AlbumCard from '@/components/cards/AlbumCard';
import SongCard from '@/components/cards/SongCard';


const MusicDiscoveryPage = () => {
  const dispatch = useAppDispatch();
  const artistLoading = useAppSelector(selectArtistLoading);
  const albumLoading = useAppSelector(selectAlbumLoading);
  const songLoading = useAppSelector(selectSongLoading);
  const loading = artistLoading && albumLoading && songLoading;
  
  const [artistTab, setArtistTab] = useState<MusicTabEnum>(MusicTabEnum.POPULAR);
  const [albumTab, setAlbumTab] = useState<MusicTabEnum>(MusicTabEnum.POPULAR);
  const [songTab, setSongTab] = useState<MusicTabEnum>(MusicTabEnum.POPULAR);
  
  const [topRatedArtists, setTopRatedArtists] = useState<Artist[]>([]);
  const [popularArtists, setPopularArtists] = useState<Artist[]>([]);
  const [topRatedAlbums, setTopRatedAlbums] = useState<Album[]>([]);
  const [popularAlbums, setPopularAlbums] = useState<Album[]>([]);
  const [topRatedSongs, setTopRatedSongs] = useState<Song[]>([]);
  const [popularSongs, setPopularSongs] = useState<Song[]>([]);

  useEffect(() => {
    const fetchDiscoveryContent = async () => {
      try {        
        // Fetch artists
        const [topArtists, popArtists] = await Promise.all([
          dispatch(fetchArtistsAsync({ page: 1, size: 10, filter: FilterTypeEnum.RATING })).unwrap(),
          dispatch(fetchArtistsAsync({ page: 1, size: 10, filter: FilterTypeEnum.POPULAR })).unwrap(),
        ]);
        setTopRatedArtists(topArtists.items.map((item) => item.data));
        setPopularArtists(popArtists.items.map((item) => item.data));
        
        // Fetch albums
        const [topAlbums, popAlbums] = await Promise.all([
          dispatch(fetchAlbumsAsync({ page: 1, size: 10, filter: FilterTypeEnum.RATING })).unwrap(),
          dispatch(fetchAlbumsAsync({ page: 1, size: 10, filter: FilterTypeEnum.POPULAR })).unwrap(),
        ]);
        setTopRatedAlbums(topAlbums.items.map((item) => item.data));
        setPopularAlbums(popAlbums.items.map((item) => item.data));
        
        // Fetch songs
        const [topSongs, popSongs] = await Promise.all([
          dispatch(fetchSongsAsync({ page: 1, size: 10, filter: FilterTypeEnum.RATING })).unwrap(),
          dispatch(fetchSongsAsync({ page: 1, size: 10, filter: FilterTypeEnum.POPULAR })).unwrap(),
        ]);
        setTopRatedSongs(topSongs.items.map((item) => item.data));
        setPopularSongs(popSongs.items.map((item) => item.data));

      } catch (error) {
        console.error('Failed to fetch discovery content:', error);
      }
    };

    fetchDiscoveryContent();
  }, [dispatch]);

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
                    className={`tab ${artistTab === MusicTabEnum.POPULAR ? 'active' : ''}`}
                    onClick={() => setArtistTab(MusicTabEnum.POPULAR)}
                    style={{ cursor: 'pointer' }}
                  >
                    Most Popular
                  </span>
                  <span
                    id="topRatedArtistButton"
                    className={`tab ${artistTab === MusicTabEnum.TOP_RATED ? 'active' : ''}`}
                    onClick={() => setArtistTab(MusicTabEnum.TOP_RATED)}
                    style={{ cursor: 'pointer' }}
                  >
                    Top Rated
                  </span>
                </div>
              </div>

              {artistTab === MusicTabEnum.TOP_RATED && topRatedArtists.length > 0 && (
                <div id="topRatedArtistTab" className="tab-content">
                  <h2>
                    Top Rated Artists
                    <Link href={`/music/view-all?tab=${ReviewItemTypeEnum.ARTIST}`} className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {topRatedArtists.map((artist) => (
                        <ArtistCard key={artist.id} artist={artist} />
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {artistTab === MusicTabEnum.POPULAR && popularArtists.length > 0 && (
                <div id="popularArtistTab" className="tab-content">
                  <h2>
                    Most Popular Artists
                    <Link href={`/music/view-all?tab=${ReviewItemTypeEnum.ARTIST}`} className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {popularArtists.map((artist) => (
                        <ArtistCard key={artist.id} artist={artist} />
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
                    className={`tab ${albumTab === MusicTabEnum.POPULAR ? 'active' : ''}`}
                    onClick={() => setAlbumTab(MusicTabEnum.POPULAR)}
                    style={{ cursor: 'pointer' }}
                  >
                    Most Popular
                  </span>
                  <span
                    id="topRatedAlbumButton"
                    className={`tab ${albumTab === MusicTabEnum.TOP_RATED ? 'active' : ''}`}
                    onClick={() => setAlbumTab(MusicTabEnum.TOP_RATED)}
                    style={{ cursor: 'pointer' }}
                  >
                    Top Rated
                  </span>
                </div>
              </div>

              {albumTab === MusicTabEnum.TOP_RATED && topRatedAlbums.length > 0 && (
                <div id="topRatedAlbumTab" className="tab-content">
                  <h2>
                    Top Rated Albums
                    <Link href={`/music/view-all?tab=${ReviewItemTypeEnum.ALBUM}`} className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {topRatedAlbums.map((album) => (
                        <AlbumCard key={album.id} album={album} />
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {albumTab === MusicTabEnum.POPULAR && popularAlbums.length > 0 && (
                <div id="popularAlbumTab" className="tab-content">
                  <h2>
                    Most Popular Albums
                    <Link href={`/music/view-all?tab=${ReviewItemTypeEnum.ALBUM}`} className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <div className="carousel-container">
                    <div className="carousel">
                      {popularAlbums.map((album) => (
                        <AlbumCard key={album.id} album={album} />
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
                      className={`tab ${songTab === MusicTabEnum.POPULAR ? 'active' : ''}`}
                    onClick={() => setSongTab(MusicTabEnum.POPULAR)}
                    style={{ cursor: 'pointer' }}
                  >
                    Most Popular
                  </span>
                  <span
                    id="topRatedSongButton"
                    className={`tab ${songTab === MusicTabEnum.TOP_RATED ? 'active' : ''}`}
                    onClick={() => setSongTab(MusicTabEnum.TOP_RATED)}
                    style={{ cursor: 'pointer' }}
                  >
                    Top Rated
                  </span>
                </div>
              </div>

              {songTab === MusicTabEnum.TOP_RATED && topRatedSongs.length > 0 && (
                <div id="topRatedSongTab" className="tab-content">
                  <h2>
                    Top Rated Songs
                    <Link href={`/music/view-all?tab=${ReviewItemTypeEnum.SONG}`} className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <ul className="song-list">
                    {topRatedSongs.map((song, index) => (
                      <SongCard key={song.id} song={song} index={index} />
                    ))}
                  </ul>
                </div>
              )}

              {songTab === MusicTabEnum.POPULAR && popularSongs.length > 0 && (
                <div id="popularSongTab" className="tab-content">
                  <h2>
                    Most Popular Songs
                    <Link href={`/music/view-all?tab=${ReviewItemTypeEnum.SONG}`} className="view-all-link">
                      View All
                    </Link>
                  </h2>
                  <ul className="song-list">
                    {popularSongs.map((song, index) => (
                      <SongCard key={song.id} song={song} index={index} />
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

