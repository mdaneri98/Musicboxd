/**
 * View All Music Page
 * Browse artists, albums, and songs with infinite scroll
 */

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { LoadingSpinner } from '@/components/ui';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import { 
  fetchArtistsAsync, 
  fetchMoreArtistsAsync,
  fetchAlbumsAsync, 
  fetchMoreAlbumsAsync,
  fetchSongsAsync, 
  fetchMoreSongsAsync,
  clearArtists,
  clearAlbums,
  clearSongs,
  selectArtistPagination,
  selectArtistLoading,
  selectArtistLoadingMore,
  selectArtistsHasMore,
  selectAlbumPagination, 
  selectAlbumLoading,
  selectAlbumLoadingMore,
  selectAlbumsHasMore,
  selectSongPagination,
  selectSongLoading,
  selectSongLoadingMore,
  selectSongsHasMore,
  selectOrderedArtists, 
  selectOrderedAlbums, 
  selectOrderedSongs 
} from '@/store/slices';
import { Artist, Album, Song, FilterTypeEnum, ReviewItemTypeEnum } from '@/types';
import { ArtistCard, AlbumCard, SongCard } from '@/components/cards';

const ViewAllMusicPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { tab: queryTab, filter: queryFilter } = router.query;
  
  const artists = useAppSelector(selectOrderedArtists);
  const albums = useAppSelector(selectOrderedAlbums);
  const songs = useAppSelector(selectOrderedSongs);

  const artistPagination = useAppSelector(selectArtistPagination);
  const albumPagination = useAppSelector(selectAlbumPagination);
  const songPagination = useAppSelector(selectSongPagination);

  const artistLoading = useAppSelector(selectArtistLoading);
  const albumLoading = useAppSelector(selectAlbumLoading);
  const songLoading = useAppSelector(selectSongLoading);

  const artistLoadingMore = useAppSelector(selectArtistLoadingMore);
  const albumLoadingMore = useAppSelector(selectAlbumLoadingMore);
  const songLoadingMore = useAppSelector(selectSongLoadingMore);

  const artistHasMore = useAppSelector(selectArtistsHasMore);
  const albumHasMore = useAppSelector(selectAlbumsHasMore);
  const songHasMore = useAppSelector(selectSongsHasMore);

  const [filter, setFilter] = useState<FilterTypeEnum>(queryFilter ? queryFilter as FilterTypeEnum : FilterTypeEnum.POPULAR);
  const [activeTab, setActiveTab] = useState<ReviewItemTypeEnum>(queryTab ? queryTab as ReviewItemTypeEnum : ReviewItemTypeEnum.ARTIST);

  // Get current tab's loading, loadingMore, hasMore, and pagination
  const loading = activeTab === ReviewItemTypeEnum.ARTIST 
    ? artistLoading 
    : activeTab === ReviewItemTypeEnum.ALBUM 
      ? albumLoading 
      : songLoading;

  const loadingMore = activeTab === ReviewItemTypeEnum.ARTIST 
    ? artistLoadingMore 
    : activeTab === ReviewItemTypeEnum.ALBUM 
      ? albumLoadingMore 
      : songLoadingMore;

  const hasMore = activeTab === ReviewItemTypeEnum.ARTIST 
    ? artistHasMore 
    : activeTab === ReviewItemTypeEnum.ALBUM 
      ? albumHasMore 
      : songHasMore;

  const pagination = activeTab === ReviewItemTypeEnum.ARTIST 
    ? artistPagination 
    : activeTab === ReviewItemTypeEnum.ALBUM 
      ? albumPagination 
      : songPagination;

  // Fetch initial data when tab or filter changes
  useEffect(() => {
    const fetchData = async () => {
      if (activeTab === ReviewItemTypeEnum.ARTIST) {
        dispatch(clearArtists());
        await dispatch(fetchArtistsAsync({ page: 1, filter })).unwrap();
      } else if (activeTab === ReviewItemTypeEnum.ALBUM) {
        dispatch(clearAlbums());
        await dispatch(fetchAlbumsAsync({ page: 1, filter })).unwrap();
      } else if (activeTab === ReviewItemTypeEnum.SONG) {
        dispatch(clearSongs());
        await dispatch(fetchSongsAsync({ page: 1, filter })).unwrap();
      }
    };

    fetchData();
  }, [activeTab, filter, dispatch]);

  // Load more callback for infinite scroll
  const handleLoadMore = useCallback(async () => {
    if (!hasMore || loadingMore) return;
    
    const nextPage = pagination.page + 1;
    
    if (activeTab === ReviewItemTypeEnum.ARTIST) {
      await dispatch(fetchMoreArtistsAsync({ page: nextPage, filter }));
    } else if (activeTab === ReviewItemTypeEnum.ALBUM) {
      await dispatch(fetchMoreAlbumsAsync({ page: nextPage, filter }));
    } else if (activeTab === ReviewItemTypeEnum.SONG) {
      await dispatch(fetchMoreSongsAsync({ page: nextPage, filter }));
    }
  }, [dispatch, activeTab, pagination.page, filter, hasMore, loadingMore]);

  // Infinite scroll hook
  const { sentinelRef, isFetchingMore } = useInfiniteScroll({
    onLoadMore: handleLoadMore,
    hasMore,
    isLoading: loading || loadingMore,
    enabled: !loading,
  });

  const handleTabChange = (tab: ReviewItemTypeEnum) => {
    if (tab !== activeTab) {
      setActiveTab(tab);
    }
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newFilter = e.target.value as FilterTypeEnum;
    setFilter(newFilter);
  };

  // Get current data based on active tab
  const currentData = activeTab === ReviewItemTypeEnum.ARTIST 
    ? artists 
    : activeTab === ReviewItemTypeEnum.ALBUM 
      ? albums 
      : songs;

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
            </form>
          </div>
        </div>

        {/* Content Grid */}
        <div className="view-all-content">
          {loading && currentData.length === 0 ? (
            <LoadingSpinner size="large" />
          ) : (
            <>
              {activeTab === ReviewItemTypeEnum.ARTIST && (
                <div className="music-grid">
                  {artists.map((artist: Artist) => (
                    <ArtistCard key={artist.id} artist={artist} />
                  ))}
                </div>
              )}

              {activeTab === ReviewItemTypeEnum.ALBUM && (
                <div className="music-grid">
                  {albums.map((album: Album) => (
                    <AlbumCard key={album.id} album={album} />
                  ))}
                </div>
              )}

              {activeTab === ReviewItemTypeEnum.SONG && (
                <ul className="song-list">
                  {songs.map((song: Song, index: number) => (
                    <SongCard key={song.id} song={song} index={index} />
                  ))}
                </ul>
              )}

              {/* Sentinel element for infinite scroll */}
              <div ref={sentinelRef} className="infinite-scroll-sentinel" />

              {/* Loading indicator for more content */}
              {(loadingMore || isFetchingMore) && (
                <div className="loading-more">
                  <LoadingSpinner size="small" />
                </div>
              )}

              {/* End of content message */}
              {!hasMore && currentData.length > 0 && (
                <div className="end-of-content">
                  <p>{t('common.noMoreContent')}</p>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default ViewAllMusicPage;
