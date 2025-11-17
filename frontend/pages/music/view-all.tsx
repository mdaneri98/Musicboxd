import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchArtistsAsync, fetchAlbumsAsync, fetchSongsAsync, selectArtistPagination, selectAlbumPagination, selectSongPagination, selectArtists, selectAlbums, selectSongs } from '@/store/slices';
import { imageRepository } from '@/repositories';
import { Artist, Album, Song, FilterTypeEnum, ReviewItemTypeEnum } from '@/types';

const ViewAllMusicPage = () => {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { tab: queryTab, filter: queryFilter, page: queryPage } = router.query;
  
  const artists = useAppSelector(selectArtists);
  const albums = useAppSelector(selectAlbums);
  const songs = useAppSelector(selectSongs);

  const artistPagination = useAppSelector(selectArtistPagination);
  const albumPagination = useAppSelector(selectAlbumPagination);
  const songPagination = useAppSelector(selectSongPagination);

  const [page, setPage] = useState<number>(queryPage ? parseInt(queryPage as string) : 1);
  const [filter, setFilter] = useState<FilterTypeEnum>(queryFilter ? queryFilter as FilterTypeEnum : FilterTypeEnum.POPULAR);
  const [activeTab, setActiveTab] = useState<ReviewItemTypeEnum>(queryTab ? queryTab as ReviewItemTypeEnum : ReviewItemTypeEnum.ARTIST);

  const [hasMore, setHasMore] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(true);


  // Fetch data based on active tab and filter
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        if (activeTab === ReviewItemTypeEnum.ARTIST) {
          await dispatch(fetchArtistsAsync({ 
            page: page, 
            filter: filter 
          })).unwrap();
          setHasMore(page*artistPagination.size < artistPagination.totalCount);
        } else if (activeTab === ReviewItemTypeEnum.ALBUM) {
          await dispatch(fetchAlbumsAsync({ 
            page: page,  
            filter: filter 
          })).unwrap();
          setHasMore(page*albumPagination.size < albumPagination.totalCount);
        } else if (activeTab === ReviewItemTypeEnum.SONG) {
          await dispatch(fetchSongsAsync({ 
            page: page, 
            filter: filter 
          })).unwrap();
          setHasMore(page*songPagination.size < songPagination.totalCount);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [activeTab, filter, page, dispatch]);

  const handleTabChange = (tab: ReviewItemTypeEnum) => {
    setActiveTab(tab);
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newFilter = e.target.value as FilterTypeEnum;
    setFilter(newFilter);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
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
            <form className="filters-form">
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
                  <option value={FilterTypeEnum.POPULAR}>Most Popular</option>
                  <option value={FilterTypeEnum.RATING}>Top Rated</option>
                  <option value={FilterTypeEnum.RECENT}>Recently Added</option>
                  <option value={FilterTypeEnum.FIRST}>First Added</option>
                  {activeTab === ReviewItemTypeEnum.ALBUM && (
                    <>
                      <option value={FilterTypeEnum.NEWEST}>Newest</option>
                      <option value={FilterTypeEnum.OLDEST}>Oldest</option>
                    </>
                  )}
                </select>
              </div>

              {/* <button type="submit" className="btn btn-primary">
                Apply
              </button> */}
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
                  {Object.values(artists).map((artist: Artist) => (
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
              )}

                {activeTab === ReviewItemTypeEnum.ALBUM && (
                <div className="music-grid">
                  {Object.values(albums).map((album: Album) => (
                    <div key={album.id} className="music-item">
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
              )}

              {activeTab === ReviewItemTypeEnum.SONG && (
                <ul className="song-list">
                  {Object.values(songs).map((song: Song) => (
                    <li key={song.id}>
                      <Link href={`/songs/${song.id}`} className="song-item">
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

