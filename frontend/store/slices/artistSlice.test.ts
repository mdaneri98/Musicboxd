/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import artistReducer, {
    clearError,
    clearCurrentArtist,
    clearArtists,
    addArtist,
    removeArtist,
    fetchArtistsAsync,
    fetchMoreArtistsAsync,
    fetchArtistByIdAsync,
    createArtistAsync,
    updateArtistAsync,
    deleteArtistAsync,
    fetchArtistAlbumsAsync,
    fetchArtistSongsAsync,
    fetchArtistReviewsAsync,
    fetchMoreArtistReviewsAsync,
    fetchUserArtistReviewAsync,
    createArtistReviewAsync,
    addArtistFavoriteAsync,
    removeArtistFavoriteAsync,
    ArtistState,
} from './artistSlice';
import { Artist, Album, Song, Review } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('artistSlice', () => {
    const initialState: ArtistState = {
        artists: {},
        orderedArtistsIds: [],
        currentArtist: null,
        artistAlbums: [],
        artistSongs: [],
        artistReviews: [],
        currentUserReview: null,
        pagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        reviewsPagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        loading: false,
        loadingMore: false,
        loadingArtist: false,
        loadingAlbums: false,
        loadingSongs: false,
        loadingReviews: false,
        loadingMoreReviews: false,
        error: null,
    };

    const mockArtist: Artist = {
        id: 1,
        name: 'Test Artist',
        bio: 'Bio',
        imageSrc: 'test.jpg',
        favorite: false,
        self: '/artists/1',
    } as any;

    const mockAlbum: Album = {
        id: 101,
        name: 'Test Album',
        artist: { id: 1, name: 'Test Artist', self: '/artists/1' },
        releaseDate: '2023-01-01',
    } as any;

    const mockSong: Song = {
        id: 201,
        name: 'Test Song',
        artist: { id: 1, name: 'Test Artist', self: '/artists/1' },
    } as any;

    const mockReview: Review = {
        id: 301,
        title: 'Good Artist',
        score: 5,
        user: { id: 1, username: 'user1' } as any,
    } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(artistReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const state = { ...initialState, error: 'Error' };
            const actual = artistReducer(state, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle clearCurrentArtist', () => {
            const state: ArtistState = {
                ...initialState,
                currentArtist: mockArtist,
                artistAlbums: [mockAlbum],
                artistSongs: [mockSong],
                artistReviews: [mockReview],
            };
            const actual = artistReducer(state, clearCurrentArtist());
            expect(actual.currentArtist).toBeNull();
            expect(actual.artistAlbums).toEqual([]);
            expect(actual.artistSongs).toEqual([]);
            expect(actual.artistReviews).toEqual([]);
        });

        it('should handle clearArtists', () => {
            const state: ArtistState = {
                ...initialState,
                artists: { 1: mockArtist },
                orderedArtistsIds: [1],
                pagination: { ...initialState.pagination, page: 2 },
            };
            const actual = artistReducer(state, clearArtists());
            expect(actual.artists).toEqual({});
            expect(actual.orderedArtistsIds).toEqual([]);
            expect(actual.pagination.page).toBe(1);
        });

        it('should handle addArtist', () => {
            const actual = artistReducer(initialState, addArtist(mockArtist));
            expect(actual.artists[1]).toEqual(mockArtist);
            expect(actual.orderedArtistsIds).toContain(1);
        });

        it('should handle removeArtist', () => {
            const state = {
                ...initialState,
                artists: { 1: mockArtist },
                orderedArtistsIds: [1],
            };
            const actual = artistReducer(state, removeArtist(1));
            expect(actual.artists[1]).toBeUndefined();
            expect(actual.orderedArtistsIds).not.toContain(1);
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ artists: ArtistState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { artists: artistReducer },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('fetchArtistsAsync', () => {
            it('should fetch artists successfully', async () => {
                const response = {
                    items: [{ data: mockArtist }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/artists')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchArtistsAsync({}));
                const state = store.getState().artists;
                expect(state.loading).toBe(false);
                expect(state.artists[1]).toEqual(mockArtist);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchArtistsAsync({}));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreArtistsAsync', () => {
            it('should append artists successfully', async () => {
                store = configureStore({
                    reducer: { artists: artistReducer },
                    preloadedState: {
                        artists: {
                            ...initialState,
                            artists: { 1: mockArtist },
                            orderedArtistsIds: [1],
                        }
                    }
                });

                const nextArtist = { ...mockArtist, id: 2 };
                const response = {
                    items: [{ data: nextArtist }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/artists')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreArtistsAsync({ page: 2 }));
                const state = store.getState().artists;
                expect(state.artists[2]).toBeDefined();
                expect(state.orderedArtistsIds).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreArtistsAsync({ page: 2 }));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchArtistByIdAsync', () => {
            it('should fetch artist successfully', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1')
                    .reply(200, { data: mockArtist });

                await store.dispatch(fetchArtistByIdAsync(1));
                const state = store.getState().artists;
                expect(state.currentArtist).toEqual(mockArtist);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1')
                    .reply(500);

                await store.dispatch(fetchArtistByIdAsync(1));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('createArtistAsync', () => {
            it('should create artist successfully', async () => {
                nock(API_BASE_URL)
                    .post('/artists')
                    .reply(201, { data: mockArtist });

                await store.dispatch(createArtistAsync({ name: 'New' } as any));
                const state = store.getState().artists;
                expect(state.artists[1]).toEqual(mockArtist);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/artists')
                    .reply(500);

                await store.dispatch(createArtistAsync({ name: 'New' } as any));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('updateArtistAsync', () => {
            it('should update artist successfully', async () => {
                store = configureStore({
                    reducer: { artists: artistReducer },
                    preloadedState: {
                        artists: {
                            ...initialState,
                            artists: { 1: mockArtist },
                            currentArtist: mockArtist,
                        }
                    }
                });

                const updated = { ...mockArtist, name: 'Updated' };
                nock(API_BASE_URL)
                    .put('/artists/1')
                    .reply(200, { data: updated });

                await store.dispatch(updateArtistAsync({ id: 1, artistData: {} as any }));
                const state = store.getState().artists;
                expect(state.artists[1].name).toBe('Updated');
                expect(state.currentArtist?.name).toBe('Updated');
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .put('/artists/1')
                    .reply(500);

                await store.dispatch(updateArtistAsync({ id: 1, artistData: {} as any }));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('deleteArtistAsync', () => {
            it('should delete artist successfully', async () => {
                store = configureStore({
                    reducer: { artists: artistReducer },
                    preloadedState: {
                        artists: {
                            ...initialState,
                            artists: { 1: mockArtist },
                            currentArtist: mockArtist,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/artists/1')
                    .reply(204);

                await store.dispatch(deleteArtistAsync(1));
                const state = store.getState().artists;
                expect(state.artists[1]).toBeUndefined();
                expect(state.currentArtist).toBeNull();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/artists/1')
                    .reply(500);

                await store.dispatch(deleteArtistAsync(1));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchArtistAlbumsAsync', () => {
            it('should fetch albums successfully', async () => {
                const response = {
                    items: [{ data: mockAlbum }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/artists/1/albums')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchArtistAlbumsAsync({ artistId: 1 }));
                const state = store.getState().artists;
                expect(state.artistAlbums).toHaveLength(1);
                expect(state.artistAlbums[0]).toEqual(mockAlbum);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1/albums')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchArtistAlbumsAsync({ artistId: 1 }));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchArtistSongsAsync', () => {
            it('should fetch songs successfully', async () => {
                const response = {
                    items: [{ data: mockSong }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/artists/1/songs')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchArtistSongsAsync({ artistId: 1 }));
                const state = store.getState().artists;
                expect(state.artistSongs).toHaveLength(1);
                expect(state.artistSongs[0]).toEqual(mockSong);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1/songs')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchArtistSongsAsync({ artistId: 1 }));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchArtistReviewsAsync', () => {
            it('should fetch reviews successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchArtistReviewsAsync({ artistId: 1 }));
                const state = store.getState().artists;
                expect(state.artistReviews).toHaveLength(1);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchArtistReviewsAsync({ artistId: 1 }));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreArtistReviewsAsync', () => {
            it('should append reviews successfully', async () => {
                store = configureStore({
                    reducer: { artists: artistReducer },
                    preloadedState: {
                        artists: {
                            ...initialState,
                            artistReviews: [mockReview],
                        }
                    }
                });

                const nextReview = { ...mockReview, id: 302 };
                const response = {
                    items: [{ data: nextReview }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreArtistReviewsAsync({ artistId: 1, page: 2 }));
                const state = store.getState().artists;
                expect(state.artistReviews).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreArtistReviewsAsync({ artistId: 1, page: 2 }));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchUserArtistReviewAsync', () => {
            it('should fetch user review successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query({ userId: 99 })
                    .reply(200, response);

                await store.dispatch(fetchUserArtistReviewAsync({ artistId: 1, userId: 99 }));
                const state = store.getState().artists;
                expect(state.currentUserReview).toEqual(mockReview);
            });

            it('should handle no review found', async () => {
                const response = {
                    items: [],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 0
                };

                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query({ userId: 99 })
                    .reply(200, response);

                await store.dispatch(fetchUserArtistReviewAsync({ artistId: 1, userId: 99 }));
                const state = store.getState().artists;
                expect(state.currentUserReview).toBeNull();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/artists/1/reviews')
                    .query({ userId: 99 })
                    .reply(500);

                await store.dispatch(fetchUserArtistReviewAsync({ artistId: 1, userId: 99 }));
                const state = store.getState().artists;
                expect(state.currentUserReview).toBeNull();
            });
        });

        describe('createArtistReviewAsync', () => {
            it('should create review successfully', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(201, { data: mockReview });

                const result = await store.dispatch(createArtistReviewAsync({} as any));
                expect(result.meta.requestStatus).toBe('fulfilled');
                expect(result.payload).toEqual({ data: mockReview });
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(500);

                const result = await store.dispatch(createArtistReviewAsync({} as any));
                expect(result.meta.requestStatus).toBe('rejected');
            });
        });

        describe('addArtistFavoriteAsync', () => {
            it('should add favorite and update artist', async () => {
                store = configureStore({
                    reducer: { artists: artistReducer },
                    preloadedState: {
                        artists: {
                            ...initialState,
                            currentArtist: mockArtist,
                            artists: { 1: mockArtist }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .post('/artists/1/favorites')
                    .reply(200);

                const favored = { ...mockArtist, favorite: true };
                nock(API_BASE_URL)
                    .get('/artists/1')
                    .reply(200, { data: favored });

                await store.dispatch(addArtistFavoriteAsync(1));
                const state = store.getState().artists;
                expect(state.currentArtist?.favorite).toBe(true);
                expect(state.artists[1].favorite).toBe(true);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/artists/1/favorites')
                    .reply(500);

                await store.dispatch(addArtistFavoriteAsync(1));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

        describe('removeArtistFavoriteAsync', () => {
            it('should remove favorite and update artist', async () => {
                store = configureStore({
                    reducer: { artists: artistReducer },
                    preloadedState: {
                        artists: {
                            ...initialState,
                            currentArtist: { ...mockArtist, favorite: true },
                            artists: { 1: { ...mockArtist, favorite: true } }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/artists/1/favorites')
                    .reply(204);

                nock(API_BASE_URL)
                    .get('/artists/1')
                    .reply(200, { data: mockArtist });

                await store.dispatch(removeArtistFavoriteAsync(1));
                const state = store.getState().artists;
                expect(state.currentArtist?.favorite).toBe(false);
                expect(state.artists[1].favorite).toBe(false);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/artists/1/favorites')
                    .reply(500);

                await store.dispatch(removeArtistFavoriteAsync(1));
                const state = store.getState().artists;
                expect(state.error).toBeDefined();
            });
        });

    });
});
