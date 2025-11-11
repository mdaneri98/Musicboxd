import { useState, useEffect, useCallback, useRef } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { UserCard } from '@/components/cards';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated } from '@/store/slices';
import {
  artistRepository,
  albumRepository,
  songRepository,
  userRepository,
  imageRepository,
} from '@/repositories';
import { Artist, Album, Song, User } from '@/types';

type SearchResultItem = {
  id: number;
  name: string;
  type: 'artist' | 'album' | 'song' | 'user';
  imgId?: number;
  imageUrl?: string;
};

type TabType = 'music' | 'users';

export default function SearchPage() {
  const router = useRouter();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  
  const [activeTab, setActiveTab] = useState<TabType>('music');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<SearchResultItem[]>([]);
  const [recommendedUsers, setRecommendedUsers] = useState<User[]>([]);
  const [showResults, setShowResults] = useState(false);
  const [currentFocus, setCurrentFocus] = useState(-1);
  const [error, setError] = useState<string | null>(null);
  
  const searchWrapperRef = useRef<HTMLDivElement>(null);
  const searchInputRef = useRef<HTMLInputElement>(null);

  // Redirect to landing if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    }
  }, [isAuthenticated, router]);

  // Fetch recommended users on mount
  useEffect(() => {
    const fetchRecommendedUsers = async () => {
      try {
        const usersData = await userRepository.getUsers(0, 6);
        setRecommendedUsers(usersData.items);
      } catch (error) {
        console.error('Failed to fetch recommended users:', error);
      }
    };

    if (isAuthenticated) {
      fetchRecommendedUsers();
    }
  }, [isAuthenticated]);

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
  const performSearch = useCallback(async (query: string) => {
    if (!query.trim()) {
      setSearchResults([]);
      setShowResults(false);
      setError(null);
      return;
    }

    try {
      setError(null);

      const [artistsData, albumsData, songsData, usersData] = await Promise.all([
        artistRepository.getArtists(0, 10, query),
        albumRepository.getAlbums(0, 10, query),
        songRepository.getSongs(0, 10, query),
        userRepository.getUsers(0, 10, query),
      ]);

      const artistResults: SearchResultItem[] = artistsData.items.map((artist: Artist) => ({
        id: artist.id,
        name: artist.name,
        type: 'artist' as const,
        imgId: artist.imageId,
        imageUrl: artist.imageId ? imageRepository.getImageUrl(artist.imageId) : undefined,
      }));

      const albumResults: SearchResultItem[] = albumsData.items.map((album: Album) => ({
        id: album.id,
        name: album.title,
        type: 'album' as const,
        imgId: album.imageId,
        imageUrl: album.imageId ? imageRepository.getImageUrl(album.imageId) : undefined,
      }));

      const songResults: SearchResultItem[] = songsData.items.map((song: Song) => ({
        id: song.id,
        name: song.title,
        type: 'song' as const,
        imgId: undefined, // Song model doesn't include album image
        imageUrl: undefined,
      }));

      const userResults: SearchResultItem[] = usersData.items.map((user: User) => ({
        id: user.id,
        name: user.name || user.username,
        type: 'user' as const,
        imgId: user.imageId,
        imageUrl: user.imageId ? imageRepository.getImageUrl(user.imageId) : undefined,
      }));

      // Filter and sort based on active tab
      let results: SearchResultItem[] = [];
      if (activeTab === 'music') {
        results = [...artistResults, ...albumResults, ...songResults];
      } else {
        results = userResults;
      }

      // Sort by relevance (items that start with query first)
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

      setSearchResults(results.slice(0, 7)); // Limit to 7 results
      setShowResults(true);

      if (results.length === 0) {
        setError('No se encontraron resultados');
      }
    } catch (error) {
      console.error('Failed to perform search:', error);
      setError('Error al buscar');
      setSearchResults([]);
    }
  }, [activeTab]);

  // Handle search input change
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchQuery(value);
    setCurrentFocus(-1);
    performSearch(value);
  };

  // Handle tab change
  const handleTabChange = (tab: TabType) => {
    setActiveTab(tab);
    setShowResults(false);
    setError(null);
    setCurrentFocus(-1);
    if (searchQuery.trim()) {
      performSearch(searchQuery);
    }
  };

  // Handle result click
  const handleResultClick = (result: SearchResultItem) => {
    setSearchQuery(result.name);
    setShowResults(false);
    setError(null);
    router.push(`/${result.type}s/${result.id}`);
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

  if (!isAuthenticated) {
    return null; // Will redirect in useEffect
  }

  return (
    <Layout title="Search - Musicboxd">

      <div className="search-container">
        <h1 className="search-title">Musicboxd</h1>

        {/* Tabs */}
        <div className="tabs">
          <span
            className={`tab ${activeTab === 'music' ? 'active' : ''}`}
            onClick={() => handleTabChange('music')}
          >
            Music
          </span>
          <span
            className={`tab ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => handleTabChange('users')}
          >
            Users
          </span>
        </div>

        {/* Search Input */}
        <div className="search-wrapper" ref={searchWrapperRef}>
          <input
            ref={searchInputRef}
            type="text"
            className="form-control search-input"
            id="searchInput"
            placeholder="Search..."
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
      </div>

      {/* Recommended Users */}
      {!searchQuery && recommendedUsers.length > 0 && (
        <>
          <h1 className="page-title">Recommended Users</h1>
          <div className="users-grid">
            {recommendedUsers.map((user) => (
              <UserCard key={user.id} user={user} />
            ))}
          </div>
        </>
      )}
    </Layout>
  );
}

