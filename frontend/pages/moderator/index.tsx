import { useState, useEffect, useCallback, useRef } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { artistRepository, albumRepository, imageRepository } from '@/repositories';
import { Artist, Album } from '@/types';

type TabType = 'artists' | 'albums' | 'songs';

type SearchResultItem = {
  id: number;
  name: string;
  type: 'artist' | 'album';
  imgId?: number;
  imageUrl?: string;
};

export default function ModeratorDashboardPage() {
  const router = useRouter();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [activeTab, setActiveTab] = useState<TabType>('artists');
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
    } else if (currentUser && !currentUser.isModerator) {
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

        if (activeTab === 'albums') {
          // Search for artists (to select artist for album)
          const artistsData = await artistRepository.getArtists(0, 10, query);

          const results: SearchResultItem[] = artistsData.items.map((artist: Artist) => ({
            id: artist.id,
            name: artist.name,
            type: 'artist' as const,
            imgId: artist.imageId,
            imageUrl: artist.imageId ? imageRepository.getImageUrl(artist.imageId) : undefined,
          }));

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
            setError('No se encontraron resultados');
          }
        } else if (activeTab === 'songs') {
          // Search for albums (to select album for song)
          const albumsData = await albumRepository.getAlbums(0, 10, query);

          const results: SearchResultItem[] = albumsData.items.map((album: Album) => ({
            id: album.id,
            name: album.title,
            type: 'album' as const,
            imgId: album.imageId,
            imageUrl: album.imageId ? imageRepository.getImageUrl(album.imageId) : undefined,
          }));

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
            setError('No se encontraron resultados');
          }
        }
      } catch (error) {
        console.error('Failed to perform search:', error);
        setError('Error al buscar');
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
  const handleTabChange = (tab: TabType) => {
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
        setError('No se encontraron resultados');
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
      case 'artists':
        router.push('/moderator/add-artist');
        break;
      case 'albums':
        if (selectedItem && selectedItem.type === 'artist') {
          router.push(`/moderator/add-album?artistId=${selectedItem.id}`);
        }
        break;
      case 'songs':
        if (selectedItem && selectedItem.type === 'album') {
          router.push(`/moderator/add-song?albumId=${selectedItem.id}`);
        }
        break;
    }
  };

  if (!isAuthenticated || (currentUser && !currentUser.isModerator)) {
    return null; // Will redirect in useEffect
  }

  const showSearchWrapper = activeTab !== 'artists';
  const buttonText =
    activeTab === 'artists' ? 'Add Artist' : activeTab === 'albums' ? 'Add Album' : 'Add Song';
  const searchPlaceholder =
    activeTab === 'albums' ? 'Search for artist...' : 'Search for album...';

  return (
    <Layout title="Moderator Dashboard - Musicboxd">
      <div className="search-container">
        <h1 className="search-title">Moderator</h1>

        {/* Tabs */}
        <div className="tabs">
          <span
            className={`tab ${activeTab === 'artists' ? 'active' : ''}`}
            onClick={() => handleTabChange('artists')}
          >
            Artist
          </span>
          <span
            className={`tab ${activeTab === 'albums' ? 'active' : ''}`}
            onClick={() => handleTabChange('albums')}
          >
            Album
          </span>
          <span
            className={`tab ${activeTab === 'songs' ? 'active' : ''}`}
            onClick={() => handleTabChange('songs')}
          >
            Song
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
            disabled={activeTab !== 'artists' && !selectedItem}
          >
            {buttonText}
          </button>
        </div>
      </div>
    </Layout>
  );
}

