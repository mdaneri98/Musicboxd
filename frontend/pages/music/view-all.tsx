import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { artistRepository, albumRepository, songRepository, imageRepository } from '@/repositories';
import { Artist, Album, Song, FilterTypeEnum, ReviewItemTypeEnum } from '@/types';


const ViewAllMusicPage = () => {
  const router = useRouter();
  const { tab: queryTab, filter: queryFilter, page: queryPage } = router.query;
  
  const [activeTab, setActiveTab] = useState<ReviewItemTypeEnum>(ReviewItemTypeEnum.ARTIST);
  const [filter, setFilter] = useState<FilterTypeEnum>(FilterTypeEnum.POPULAR);
  const [page, setPage] = useState(1);
  
  const [artists, setArtists] = useState<Artist[]>([]);
  const [albums, setAlbums] = useState<Album[]>([]);
  const [songs, setSongs] = useState<Song[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [hasMore, setHasMore] = useState(true);

  // Update state from query params
  useEffect(() => {
    if (queryTab) {
      setActiveTab(queryTab as ReviewItemTypeEnum);
    }
    if (queryFilter) {
      setFilter(queryFilter as FilterTypeEnum);
    }
    if (queryPage) {
      setPage(parseInt(queryPage as string));
    }
  }, [queryTab, queryFilter, queryPage]);

  // Fetch data based on active tab and filter
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const pageSize = 20;
        

        if (activeTab === ReviewItemTypeEnum.ARTIST) {
          const response = await artistRepository.getArtists(page, pageSize, undefined, FilterTypeEnum.POPULAR);
          setArtists(response.items.map((item) => item.data));
          setHasMore(response.items.length === pageSize);
        } else if (activeTab === ReviewItemTypeEnum.ALBUM) {
          const response = await albumRepository.getAlbums(page, pageSize, undefined, FilterTypeEnum.POPULAR);
          setAlbums(response.items.map((item) => item.data));
          setHasMore(response.items.length === pageSize);
        } else if (activeTab === ReviewItemTypeEnum.SONG) {
          const response = await songRepository.getSongs(page, pageSize, undefined, FilterTypeEnum.POPULAR);
          setSongs(response.items.map((item) => item.data));
          setHasMore(response.items.length === pageSize);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [activeTab, filter, page]);

  const handleTabChange = (tab: ReviewItemTypeEnum) => {
    setActiveTab(tab);
    setPage(1);
    router.push(`/music/view-all?tab=${tab}&filter=${filter}&page=1`, undefined, { shallow: true });
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newFilter = e.target.value as FilterTypeEnum;
    setFilter(newFilter);
    setPage(1);
    router.push(`/music/view-all?tab=${activeTab}&filter=${newFilter}&page=1`, undefined, { shallow: true });
  };

  const handleApplyFilter = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(1);
    router.push(`/music/view-all?tab=${activeTab}&filter=${filter}&page=1`, undefined, { shallow: true });
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    router.push(`/music/view-all?tab=${activeTab}&filter=${filter}&page=${newPage}`, undefined, { shallow: true });
  };

  return (
    <Layout title="Musicboxd - Browse Music">
      <div className="content-wrapper">
        {/* Tabs Navigation */}
        <div className="view-all-header">
          <div className="tabs-container">
            <div className="tabs">
              <span
                className={`tab ${activeTab === ReviewItemTypeEnum.ARTIST ? 'active' : ''}`}
                onClick={() => handleTabChange(ReviewItemTypeEnum.ARTIST)}
                style={{ cursor: 'pointer' }}
              >
                Artists
              </span>
              <span
                className={`tab ${activeTab === ReviewItemTypeEnum.ALBUM ? 'active' : ''}`}
                onClick={() => handleTabChange(ReviewItemTypeEnum.ALBUM)}
                style={{ cursor: 'pointer' }}
              >
                Albums
              </span>
              <span
                className={`tab ${activeTab === ReviewItemTypeEnum.SONG ? 'active' : ''}`}
                onClick={() => handleTabChange(ReviewItemTypeEnum.SONG)}
                style={{ cursor: 'pointer' }}
              >
                Songs
              </span>
            </div>
          </div>

          {/* Filters */}
          <div className="filters-container">
            <form onSubmit={handleApplyFilter} className="filters-form">
              <div className="filter-group">
                <label htmlFor="sort" className="filter-label">
                  Sort by
                </label>
                <select
                  name="filter"
                  id="sort"
                  className="filter-select"
                  value={filter}
                  onChange={handleFilterChange}
                >
                  <option value="POPULAR">Most Popular</option>
                  <option value="RATING">Top Rated</option>
                  <option value="RECENT">Recently Added</option>
                  <option value="FIRST">First Added</option>
                  {activeTab === ReviewItemTypeEnum.ALBUM && (
                    <>
                      <option value="NEWEST">Newest</option>
                      <option value="OLDEST">Oldest</option>
                    </>
                  )}
                </select>
              </div>

              <button type="submit" className="btn btn-primary">
                Apply
              </button>
            </form>
          </div>
        </div>

        {/* Content Grid */}
        <div className="view-all-content">
          {loading ? (
            <div className="loading">Loading...</div>
          ) : (
            <>
              {activeTab === ReviewItemTypeEnum.ARTIST && (
                <div className="music-grid">
                  {artists.map((artist) => (
                    <div key={artist.id} className="music-item artist-item">
                      <Link href={`/artists/${artist.id}`} className="music-item-link">
                        <div className="music-item-image-container">
                          <img
                            src={artist.imageId ? imageRepository.getImageUrl(artist.imageId) : '/assets/default-artist.png'}
                            alt={artist.name}
                            className="music-item-image"
                          />
                          {artist.averageRating !== undefined && (
                            <div className="rating-badge">
                              <span className="rating">{artist.averageRating.toFixed(1)}</span>
                              <span className="star">&#9733;</span>
                            </div>
                          )}
                        </div>
                        <p className="music-item-title">{artist.name}</p>
                      </Link>
                    </div>
                  ))}
                </div>
              )}

                {activeTab === ReviewItemTypeEnum.ALBUM && (
                <div className="music-grid">
                  {albums.map((album) => (
                    <div key={album.id} className="music-item">
                      <Link href={`/albums/${album.id}`} className="music-item-link">
                        <div className="music-item-image-container">
                          <img
                            src={album.imageId ? imageRepository.getImageUrl(album.imageId) : '/assets/default-album.png'}
                            alt={album.title}
                            className="music-item-image"
                          />
                          {album.averageRating !== undefined && (
                            <div className="rating-badge">
                              <span className="rating">{album.averageRating.toFixed(1)}</span>
                              <span className="star">&#9733;</span>
                            </div>
                          )}
                        </div>
                        <p className="music-item-title">{album.title}</p>
                      </Link>
                    </div>
                  ))}
                </div>
              )}

              {activeTab === ReviewItemTypeEnum.SONG && (
                <ul className="song-list">
                  {songs.map((song) => (
                    <li key={song.id}>
                      <Link href={`/songs/${song.id}`} className="song-item">
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
              )}

              {/* Pagination */}
              <div className="pagination">
                {page > 1 && (
                  <button
                    onClick={() => handlePageChange(page - 1)}
                    className="btn btn-secondary"
                  >
                    Previous Page
                  </button>
                )}
                {hasMore && (
                  <button
                    onClick={() => handlePageChange(page + 1)}
                    className="btn btn-secondary"
                  >
                    Next Page
                  </button>
                )}
              </div>
            </>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default ViewAllMusicPage;

