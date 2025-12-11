import { useState, useEffect, useCallback, useRef } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, fetchAlbumsAsync } from '@/store/slices';
import { Artist, Album, HALResource } from '@/types';
import { fetchArtistsAsync } from '@/store/slices';
import { imageRepository } from '@/repositories';
import { ReviewItemType } from '@/types/enums';
import { useTranslation } from 'react-i18next';

type SearchResultItem = {
  id: number;
  name: string;
  type: ReviewItemType;
  imgId?: number;
  imageUrl?: string;
  artistId?: number;
};

export default function ModeratorDashboardPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<ReviewItemType>(ReviewItemType.ARTIST);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<SearchResultItem[]>([]);
  const [selectedItem, setSelectedItem] = useState<SearchResultItem | null>(null);
  const [showResults, setShowResults] = useState(false);
  const [currentFocus, setCurrentFocus] = useState(-1);
  const [error, setError] = useState<string | null>(null);

  const searchWrapperRef = useRef<HTMLDivElement>(null);

  // Redirect if not authenticated or not moderator
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else if (currentUser && !currentUser.moderator) {
      router.push('/');
    }
  }, [isAuthenticated, currentUser, router]);

  // Close results when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchWrapperRef.current && !searchWrapperRef.current.contains(event.target as Node)) {
        setShowResults(false);
        setError(null);
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  // Perform search
  const performSearch = useCallback(
    async (query: string) => {
      if (!query.trim()) {
        setSearchResults([]);
        setShowResults(false);
        setError(null);
        return;
      }

      try {
        setError(null);

        if (activeTab === ReviewItemType.ALBUM) {
          const artistsData = await dispatch(fetchArtistsAsync({ page: 1, size: 10, search: query })).unwrap();
          const artists = artistsData.items.map((item: HALResource<Artist>) => item.data);
          const results: SearchResultItem[] = artists.map((artist: Artist) => ({
            id: artist.id,
            name: artist.name,
            type: ReviewItemType.ARTIST,
            imgId: artist.image_id,
            imageUrl: artist.image_id ? imageRepository.getImageUrl(artist.image_id) : undefined,
            artistId: artist.id,
          })) as SearchResultItem[];

          // Sort by relevance
          const query_lower = query.toLowerCase();
          results.sort((a, b) => {
            const nameA = a.name.toLowerCase();
            const nameB = b.name.toLowerCase();

            const startsWithA = nameA.startsWith(query_lower);
            const startsWithB = nameB.startsWith(query_lower);

            if (startsWithA && !startsWithB) return -1;
            if (!startsWithA && startsWithB) return 1;

            const indexA = nameA.indexOf(query_lower);
            const indexB = nameB.indexOf(query_lower);

            return indexA - indexB;
          });

          setSearchResults(results.slice(0, 7));
          setShowResults(true);

          if (results.length === 0) {
            setError(t("search.noResults"));
          }
        } else if (activeTab === ReviewItemType.SONG) {
          const albumsData = await dispatch(fetchAlbumsAsync({ page: 1, size: 10, search: query })).unwrap();
          const albums = albumsData.items.map((item: HALResource<Album>) => item.data);
          const results: SearchResultItem[] = albums.map((album: Album) => ({
            id: album.id,
            name: album.title,
            type: ReviewItemType.ALBUM,
            imgId: album.image_id,
            imageUrl: album.image_id ? imageRepository.getImageUrl(album.image_id) : undefined,
            artistId: album.artist_id,
          })) as SearchResultItem[];

          // Sort by relevance
          const query_lower = query.toLowerCase();
          results.sort((a, b) => {
            const nameA = a.name.toLowerCase();
            const nameB = b.name.toLowerCase();

            const startsWithA = nameA.startsWith(query_lower);
            const startsWithB = nameB.startsWith(query_lower);

            if (startsWithA && !startsWithB) return -1;
            if (!startsWithA && startsWithB) return 1;

            const indexA = nameA.indexOf(query_lower);
            const indexB = nameB.indexOf(query_lower);

            return indexA - indexB;
          });

          setSearchResults(results.slice(0, 7));
          setShowResults(true);

          if (results.length === 0) {
            setError(t("search.noResults"));
          }
        }
      } catch (error) {
        console.error(t("search.searchError"), error);
        setError(t("search.searchError"));
        setSearchResults([]);
      }
    },
    [activeTab]
  );

  // Handle search input change
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchQuery(value);
    setCurrentFocus(-1);
    setSelectedItem(null);
    performSearch(value);
  };

  // Handle tab change
  const handleTabChange = (tab: ReviewItemType) => {
    setActiveTab(tab);
    setSearchQuery('');
    setSearchResults([]);
    setSelectedItem(null);
    setShowResults(false);
    setError(null);
    setCurrentFocus(-1);
  };

  // Handle result click
  const handleResultClick = (result: SearchResultItem) => {
    setSearchQuery(result.name);
    setSelectedItem(result);
    setShowResults(false);
    setError(null);
  };

  // Handle keyboard navigation
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!showResults || searchResults.length === 0) {
      if (e.key === 'Enter' && searchResults.length === 0) {
        setError(t("search.noResults"));
      }
      return;
    }

    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setCurrentFocus((prev) => (prev >= searchResults.length - 1 ? 0 : prev + 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setCurrentFocus((prev) => (prev <= 0 ? searchResults.length - 1 : prev - 1));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      const focusedResult = searchResults[currentFocus >= 0 ? currentFocus : 0];
      if (focusedResult) {
        handleResultClick(focusedResult);
      }
    }
  };

  // Handle redirect to add page
  const handleRedirect = () => {
    switch (activeTab) {
      case ReviewItemType.ARTIST:
        router.push('/moderator/music');
        break;
      case ReviewItemType.ALBUM:
        if (selectedItem && selectedItem.type === ReviewItemType.ARTIST) {
          router.push(`/moderator/music?artistId=${selectedItem.id}`);
        }
        break;
      case ReviewItemType.SONG:
        if (selectedItem && selectedItem.type === ReviewItemType.ALBUM) {
          router.push(`/moderator/music?artistId=${selectedItem.artistId}&albumId=${selectedItem.id}`);
        }
        break;
    }
  };

  // Handle edit artist (redirect to music editor with artist ID)
  const handleEditArtist = (artistId: number) => {
    router.push(`/moderator/music?artistId=${artistId}`);
  };

  if (!isAuthenticated || (currentUser && !currentUser.moderator)) {
    return null; // Will redirect in useEffect
  }

  const showSearchWrapper = activeTab !== ReviewItemType.ARTIST;
  const buttonText =
    activeTab === ReviewItemType.ARTIST ? t("label.addArtist") : activeTab === ReviewItemType.ALBUM ? t("label.addAlbum") : t("label.addSong");
  const searchPlaceholder =
    activeTab === ReviewItemType.ALBUM ? t("search.placeholder.artist") : t("search.placeholder.album");

  return (
    <Layout title={t("moderator.title")}>
      <div className="search-container">
        <h1 className="search-title">{t("label.moderator")}</h1>

        {/* Tabs */}
        <div className="tabs">
          <span
            className={`tab ${activeTab === ReviewItemType.ARTIST ? 'active' : ''}`}
            onClick={() => handleTabChange(ReviewItemType.ARTIST)}
          >
            {t("label.artist")}
          </span>
          <span
              className={`tab ${activeTab === ReviewItemType.ALBUM ? 'active' : ''}`}
            onClick={() => handleTabChange(ReviewItemType.ALBUM)}
          >
            {t("label.album")}
          </span>
          <span
            className={`tab ${activeTab === ReviewItemType.SONG ? 'active' : ''}`}
            onClick={() => handleTabChange(ReviewItemType.SONG)}
          >
            {t("label.song")}
          </span>
        </div>

        {/* Search Input (visible for albums and songs) */}
        {showSearchWrapper && (
          <div className="search-wrapper" ref={searchWrapperRef}>
            <input
              type="text"
              className="form-control search-input"
              id="searchInput"
              placeholder={searchPlaceholder}
              value={searchQuery}
              onChange={handleSearchChange}
              onKeyDown={handleKeyDown}
              autoComplete="off"
            />
            <svg className="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
              <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
            </svg>

            {/* Search Results */}
            {showResults && searchResults.length > 0 && (
              <div className="search-results-list">
                {searchResults.map((result, index) => (
                  <div
                    key={`${result.type}-${result.id}`}
                    className={`search-result-item ${index === currentFocus ? 'search-result-active' : ''}`}
                    onClick={() => handleResultClick(result)}
                  >
                    <div className="search-result-content" data-type={result.type}>
                      {result.imageUrl ? (
                        <img
                          src={result.imageUrl}
                          alt={result.name}
                          className="search-result-image"
                        />
                      ) : (
                        <div className="search-result-image placeholder" />
                      )}
                      <div className="search-result-info">
                        <span className="search-result-name">{result.name}</span>
                        <span className="search-result-type">
                          {result.type.charAt(0).toUpperCase() + result.type.slice(1)}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Error Message */}
            {error && <div className="search-error">{error}</div>}
          </div>
        )}

        {/* Add Button */}
        <div className="mod-actions">
          <button
            onClick={handleRedirect}
            className="btn btn-primary"
            disabled={activeTab !== ReviewItemType.ARTIST && !selectedItem}
          >
            {buttonText}
          </button>
        </div>
      </div>
    </Layout>
  );
}

