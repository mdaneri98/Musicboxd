import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchArtistsAsync, fetchAlbumsAsync, fetchSongsAsync, selectArtistPagination, selectAlbumPagination, selectSongPagination, selectOrderedArtists, selectOrderedAlbums, selectOrderedSongs } from '@/store/slices';
import { Artist, Album, Song, FilterTypeEnum, ReviewItemTypeEnum } from '@/types';
import { ArtistCard, AlbumCard, SongCard } from '@/components/cards';

const ViewAllMusicPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { tab: queryTab, filter: queryFilter, page: queryPage } = router.query;
  
  const artists = useAppSelector(selectOrderedArtists);
  const albums = useAppSelector(selectOrderedAlbums);
  const songs = useAppSelector(selectOrderedSongs);

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
                {t('music.artists')}
              </span>
              <span
                className={`tab ${activeTab === ReviewItemTypeEnum.ALBUM ? 'active' : ''}`}
                onClick={() => handleTabChange(ReviewItemTypeEnum.ALBUM)}
                style={{ cursor: 'pointer' }}
              >
                {t('music.albums')}
              </span>
              <span
                className={`tab ${activeTab === ReviewItemTypeEnum.SONG ? 'active' : ''}`}
                onClick={() => handleTabChange(ReviewItemTypeEnum.SONG)}
                style={{ cursor: 'pointer' }}
              >
                {t('music.songs')}
              </span>
            </div>
          </div>

          {/* Filters */}
          <div className="filters-container">
            <form className="filters-form">
              <div className="filter-group">
                <label htmlFor="sort" className="filter-label">
                  {t('music.sortBy')}
                </label>
                <select
                  name="filter"
                  id="sort"
                  className="filter-select"
                  value={filter}
                  onChange={handleFilterChange}
                >
                  <option value={FilterTypeEnum.POPULAR}>{t('music.mostPopular')}</option>
                  <option value={FilterTypeEnum.RATING}>{t('music.topRated')}</option>
                  <option value={FilterTypeEnum.RECENT}>{t('music.recentlyAdded')}</option>
                  <option value={FilterTypeEnum.FIRST}>{t('music.firstAdded')}</option>
                  {activeTab === ReviewItemTypeEnum.ALBUM && (
                    <>
                      <option value={FilterTypeEnum.NEWEST}>{t('music.newest')}</option>
                      <option value={FilterTypeEnum.OLDEST}>{t('music.oldest')}</option>
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
            <div className="loading">{t('common.loading')}</div>
          ) : (
            <>
              {activeTab === ReviewItemTypeEnum.ARTIST && (
                <div className="music-grid">
                  {Object.values(artists).map((artist: Artist) => (
                    <ArtistCard key={artist.id} artist={artist} />
                  ))}
                </div>
              )}

                {activeTab === ReviewItemTypeEnum.ALBUM && (
                <div className="music-grid">
                  {Object.values(albums).map((album: Album) => (
                    <AlbumCard key={album.id} album={album} />
                  ))}
                </div>
              )}

              {activeTab === ReviewItemTypeEnum.SONG && (
                <ul className="song-list">
                  {Object.values(songs).map((song: Song, index: number) => (
                    <SongCard key={song.id} song={song} index={index} />
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
                    {t('common.previous')} {t('music.page')}
                  </button>
                )}
                {hasMore && (
                  <button
                    onClick={() => handlePageChange(page + 1)}
                    className="btn btn-secondary"
                  >
                    {t('common.next')} {t('music.page')}
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

