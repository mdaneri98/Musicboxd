/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import searchReducer, {
    setQuery,
    setFilters,
    clearSearch,
    clearError,
    addRecentSearch,
    clearRecentSearches,
    searchAsync,
    searchUsersAsync,
    searchArtistsAsync,
    searchAlbumsAsync,
    searchSongsAsync,
    searchReviewsAsync,
    SearchState,
} from './searchSlice';
import { User, Artist, Album, Song, Review } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('searchSlice', () => {
    const initialState: SearchState = {
        query: '',
        results: {
            users: [],
            artists: [],
            albums: [],
            songs: [],
            reviews: [],
        },
        filters: {
            type: 'all',
        },
        isSearching: false,
        error: null,
        recentSearches: [],
    };

    const mockUser: User = { id: 1, username: 'User 1' } as any;
    const mockArtist: Artist = { id: 1, name: 'Artist 1' } as any;
    const mockAlbum: Album = { id: 1, name: 'Album 1' } as any;
    const mockSong: Song = { id: 1, name: 'Song 1' } as any;
    const mockReview: Review = { id: 1, title: 'Review 1' } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(searchReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle setQuery', () => {
            const actual = searchReducer(initialState, setQuery('test'));
            expect(actual.query).toBe('test');
        });

        it('should handle setFilters', () => {
            const actual = searchReducer(initialState, setFilters({ type: 'users' }));
            expect(actual.filters.type).toBe('users');
        });

        it('should handle clearSearch', () => {
            const modifiedState: SearchState = {
                ...initialState,
                query: 'test',
                results: { ...initialState.results, users: [mockUser] },
                filters: { type: 'users' },
                error: 'error',
            };
            const actual = searchReducer(modifiedState, clearSearch());
            expect(actual).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const state = { ...initialState, error: 'Error' };
            const actual = searchReducer(state, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle addRecentSearch', () => {
            let state = searchReducer(initialState, addRecentSearch('query1'));
            expect(state.recentSearches).toEqual(['query1']);

            state = searchReducer(state, addRecentSearch('query2'));
            expect(state.recentSearches).toEqual(['query2', 'query1']);

            // Duplicate
            state = searchReducer(state, addRecentSearch('query1'));
            expect(state.recentSearches).toEqual(['query1', 'query2']);
        });

        it('should limit recent searches to 10', () => {
            let state = initialState;
            for (let i = 1; i <= 15; i++) {
                state = searchReducer(state, addRecentSearch(`query${i}`));
            }
            expect(state.recentSearches).toHaveLength(10);
            expect(state.recentSearches[0]).toBe('query15');
        });

        it('should handle clearRecentSearches', () => {
            const state = { ...initialState, recentSearches: ['query1'] };
            const actual = searchReducer(state, clearRecentSearches());
            expect(actual.recentSearches).toEqual([]);
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ search: SearchState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { search: searchReducer },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('searchAsync', () => {
            it('should perform multi-entity search successfully', async () => {
                nock(API_BASE_URL)
                    .get('/users')
                    .query(true)
                    .reply(200, { items: [{ data: mockUser }] });

                nock(API_BASE_URL)
                    .get('/artists')
                    .query(true)
                    .reply(200, { items: [{ data: mockArtist }] });

                nock(API_BASE_URL)
                    .get('/albums')
                    .query(true)
                    .reply(200, { items: [{ data: mockAlbum }] });

                nock(API_BASE_URL)
                    .get('/songs')
                    .query(true)
                    .reply(200, { items: [{ data: mockSong }] });

                nock(API_BASE_URL)
                    .get('/reviews')
                    .query(true)
                    .reply(200, { items: [{ data: mockReview }] });

                store.dispatch(setQuery('test'));
                await store.dispatch(searchAsync({ query: 'test' }));
                const state = store.getState().search;
                expect(state.results.users).toContainEqual(mockUser);
                expect(state.results.artists).toContainEqual(mockArtist);
                expect(state.results.albums).toContainEqual(mockAlbum);
                expect(state.results.songs).toContainEqual(mockSong);
                expect(state.results.reviews).toContainEqual(mockReview);
                expect(state.recentSearches).toContain('test');
            });

            it('should handle failure in one of the searches', async () => {
                // Even if one fails, others might succeed, or usually Promise.all fails if one fails.
                // The implementation uses Promise.all, so if one fails, the whole thunk should fail.
                nock(API_BASE_URL).get('/users').query(true).reply(500);
                nock(API_BASE_URL).get('/artists').query(true).reply(200, { items: [] });
                nock(API_BASE_URL).get('/albums').query(true).reply(200, { items: [] });
                nock(API_BASE_URL).get('/songs').query(true).reply(200, { items: [] });
                nock(API_BASE_URL).get('/reviews').query(true).reply(200, { items: [] });

                await store.dispatch(searchAsync({ query: 'fail' }));
                const state = store.getState().search;
                expect(state.error).toBeDefined();
            });
        });

        describe('searchUsersAsync', () => {
            it('should search users successfully', async () => {
                nock(API_BASE_URL)
                    .get('/users')
                    .query(true)
                    .reply(200, { items: [{ data: mockUser }] });

                await store.dispatch(searchUsersAsync('test'));
                const state = store.getState().search;
                expect(state.results.users).toContainEqual(mockUser);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL).get('/users').query(true).reply(500);
                await store.dispatch(searchUsersAsync('fail'));
                const state = store.getState().search;
                expect(state.error).toBeDefined();
            });
        });

        describe('searchArtistsAsync', () => {
            it('should search artists successfully', async () => {
                nock(API_BASE_URL)
                    .get('/artists')
                    .query(true)
                    .reply(200, { items: [{ data: mockArtist }] });

                await store.dispatch(searchArtistsAsync('test'));
                const state = store.getState().search;
                expect(state.results.artists).toContainEqual(mockArtist);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL).get('/artists').query(true).reply(500);
                await store.dispatch(searchArtistsAsync('fail'));
                const state = store.getState().search;
                expect(state.error).toBeDefined();
            });
        });

        describe('searchAlbumsAsync', () => {
            it('should search albums successfully', async () => {
                nock(API_BASE_URL)
                    .get('/albums')
                    .query(true)
                    .reply(200, { items: [{ data: mockAlbum }] });

                await store.dispatch(searchAlbumsAsync('test'));
                const state = store.getState().search;
                expect(state.results.albums).toContainEqual(mockAlbum);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL).get('/albums').query(true).reply(500);
                await store.dispatch(searchAlbumsAsync('fail'));
                const state = store.getState().search;
                expect(state.error).toBeDefined();
            });
        });

        describe('searchSongsAsync', () => {
            it('should search songs successfully', async () => {
                nock(API_BASE_URL)
                    .get('/songs')
                    .query(true)
                    .reply(200, { items: [{ data: mockSong }] });

                await store.dispatch(searchSongsAsync('test'));
                const state = store.getState().search;
                expect(state.results.songs).toContainEqual(mockSong);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL).get('/songs').query(true).reply(500);
                await store.dispatch(searchSongsAsync('fail'));
                const state = store.getState().search;
                expect(state.error).toBeDefined();
            });
        });

        describe('searchReviewsAsync', () => {
            it('should search reviews successfully', async () => {
                nock(API_BASE_URL)
                    .get('/reviews')
                    .query(true)
                    .reply(200, { items: [{ data: mockReview }] });

                await store.dispatch(searchReviewsAsync('test'));
                const state = store.getState().search;
                expect(state.results.reviews).toContainEqual(mockReview);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL).get('/reviews').query(true).reply(500);
                await store.dispatch(searchReviewsAsync('fail'));
                const state = store.getState().search;
                expect(state.error).toBeDefined();
            });
        });

    });
});
