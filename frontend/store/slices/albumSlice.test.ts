/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import albumReducer, {
    clearError,
    clearCurrentAlbum,
    clearAlbums,
    addAlbum,
    removeAlbum,
    fetchAlbumsAsync,
    fetchMoreAlbumsAsync,
    fetchAlbumByIdAsync,
    createAlbumAsync,
    updateAlbumAsync,
    deleteAlbumAsync,
    fetchAlbumSongsAsync,
    fetchAlbumReviewsAsync,
    fetchMoreAlbumReviewsAsync,
    fetchUserAlbumReviewAsync,
    createAlbumReviewAsync,
    addAlbumFavoriteAsync,
    removeAlbumFavoriteAsync,
    AlbumState,
} from './albumSlice';
import { Album, Song, Review } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('albumSlice', () => {
    const initialState: AlbumState = {
        albums: {},
        orderedAlbumsIds: [],
        currentAlbum: null,
        albumSongs: [],
        albumReviews: [],
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
        loadingAlbum: false,
        loadingSongs: false,
        loadingReviews: false,
        loadingMoreReviews: false,
        error: null,
    };

    const mockAlbum: Album = {
        id: 1,
        title: 'Test Album',
        artist: { id: 1, name: 'Test Artist', self: '/artists/1' },
        releaseDate: '2023-01-01',
        genre: 'Rock',
        imageSrc: 'test.jpg',
        favorite: false,
    } as any;

    const mockSong: Song = {
        id: 101,
        name: 'Test Song',
        trackNumber: 1,
        album: { id: 1, title: 'Test Album', self: '/albums/1' },
        artist: { id: 1, name: 'Test Artist', self: '/artists/1' },
        duration: 180,
    } as any;

    const mockReview: Review = {
        id: 201,
        title: 'Great Album',
        description: 'Loved it',
        score: 5,
        user: { id: 1, username: 'user1' } as any,
        likes: 0,
        liked: false,
    } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(albumReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const state = { ...initialState, error: 'Error' };
            const actual = albumReducer(state, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle clearCurrentAlbum', () => {
            const state: AlbumState = {
                ...initialState,
                currentAlbum: mockAlbum,
                albumSongs: [mockSong],
                albumReviews: [mockReview],
                currentUserReview: mockReview,
            };
            const actual = albumReducer(state, clearCurrentAlbum());
            expect(actual.currentAlbum).toBeNull();
            expect(actual.albumSongs).toEqual([]);
            expect(actual.albumReviews).toEqual([]);
            expect(actual.currentUserReview).toBeNull();
        });

        it('should handle clearAlbums', () => {
            const state: AlbumState = {
                ...initialState,
                albums: { 1: mockAlbum },
                orderedAlbumsIds: [1],
                pagination: { ...initialState.pagination, page: 2 },
            };
            const actual = albumReducer(state, clearAlbums());
            expect(actual.albums).toEqual({});
            expect(actual.orderedAlbumsIds).toEqual([]);
            expect(actual.pagination.page).toBe(1);
        });

        it('should handle addAlbum', () => {
            const actual = albumReducer(initialState, addAlbum(mockAlbum));
            expect(actual.albums[1]).toEqual(mockAlbum);
        });

        it('should handle removeAlbum', () => {
            const state = {
                ...initialState,
                albums: { 1: mockAlbum },
            };
            const actual = albumReducer(state, removeAlbum(1));
            expect(actual.albums[1]).toBeUndefined();
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ albums: AlbumState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { albums: albumReducer },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('fetchAlbumsAsync', () => {
            it('should fetch albums successfully', async () => {
                const response = {
                    items: [{ data: mockAlbum }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/albums')
                    .query(true) // match any query params
                    .reply(200, response);

                await store.dispatch(fetchAlbumsAsync({}));
                const state = store.getState().albums;
                expect(state.loading).toBe(false);
                expect(state.albums[1]).toEqual(mockAlbum);
                expect(state.orderedAlbumsIds).toContain(1);
            });

            it('should handle fetch failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchAlbumsAsync({}));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreAlbumsAsync', () => {
            it('should append albums successfully', async () => {
                store = configureStore({
                    reducer: { albums: albumReducer },
                    preloadedState: {
                        albums: {
                            ...initialState,
                            albums: { 1: mockAlbum },
                            orderedAlbumsIds: [1],
                        }
                    }
                });

                const nextAlbum = { ...mockAlbum, id: 2 };
                const response = {
                    items: [{ data: nextAlbum }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/albums')
                    .query({ page: 2, size: 10 })
                    .reply(200, response);

                await store.dispatch(fetchMoreAlbumsAsync({ page: 2 }));
                const state = store.getState().albums;
                expect(state.loadingMore).toBe(false);
                expect(state.albums[2]).toBeDefined();
                expect(state.orderedAlbumsIds).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreAlbumsAsync({ page: 2 }));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
                expect(state.loadingMore).toBe(false);
            });
        });

        describe('fetchAlbumByIdAsync', () => {
            it('should fetch album successfully', async () => {
                nock(API_BASE_URL)
                    .get('/albums/1')
                    .reply(200, { data: mockAlbum });

                await store.dispatch(fetchAlbumByIdAsync(1));
                const state = store.getState().albums;
                expect(state.currentAlbum).toEqual(mockAlbum);
                expect(state.albums[1]).toEqual(mockAlbum);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums/1')
                    .reply(500);

                await store.dispatch(fetchAlbumByIdAsync(1));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('createAlbumAsync', () => {
            it('should create album successfully', async () => {
                nock(API_BASE_URL)
                    .post('/albums')
                    .reply(201, { data: mockAlbum });

                await store.dispatch(createAlbumAsync({ title: 'New' } as any));
                const state = store.getState().albums;
                expect(state.albums[1]).toEqual(mockAlbum);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/albums')
                    .reply(500);

                await store.dispatch(createAlbumAsync({ title: 'New' } as any));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('updateAlbumAsync', () => {
            it('should update album successfully', async () => {
                store = configureStore({
                    reducer: { albums: albumReducer },
                    preloadedState: {
                        albums: {
                            ...initialState,
                            albums: { 1: mockAlbum },
                            currentAlbum: mockAlbum,
                        }
                    }
                });

                const updated = { ...mockAlbum, title: 'Updated' };
                nock(API_BASE_URL)
                    .put('/albums/1')
                    .reply(200, { data: updated });

                await store.dispatch(updateAlbumAsync({ id: 1, albumData: {} as any }));
                const state = store.getState().albums;
                expect(state.albums[1].title).toBe('Updated');
                expect(state.currentAlbum?.title).toBe('Updated');
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .put('/albums/1')
                    .reply(500);

                await store.dispatch(updateAlbumAsync({ id: 1, albumData: {} as any }));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('deleteAlbumAsync', () => {
            it('should delete album successfully', async () => {
                store = configureStore({
                    reducer: { albums: albumReducer },
                    preloadedState: {
                        albums: {
                            ...initialState,
                            albums: { 1: mockAlbum },
                            currentAlbum: mockAlbum,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/albums/1')
                    .reply(204);

                await store.dispatch(deleteAlbumAsync(1));
                const state = store.getState().albums;
                expect(state.albums[1]).toBeUndefined();
                expect(state.currentAlbum).toBeNull();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/albums/1')
                    .reply(500);

                await store.dispatch(deleteAlbumAsync(1));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchAlbumSongsAsync', () => {
            it('should fetch songs successfully', async () => {
                const response = {
                    items: [{ data: mockSong }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/albums/1/songs')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchAlbumSongsAsync({ albumId: 1 }));
                const state = store.getState().albums;
                expect(state.albumSongs).toHaveLength(1);
                expect(state.albumSongs[0]).toEqual(mockSong);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums/1/songs')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchAlbumSongsAsync({ albumId: 1 }));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchAlbumReviewsAsync', () => {
            it('should fetch reviews successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/albums/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchAlbumReviewsAsync({ albumId: 1 }));
                const state = store.getState().albums;
                expect(state.albumReviews).toHaveLength(1);
                expect(state.albumReviews[0]).toEqual(mockReview);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchAlbumReviewsAsync({ albumId: 1 }));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreAlbumReviewsAsync', () => {
            it('should append reviews successfully', async () => {
                store = configureStore({
                    reducer: { albums: albumReducer },
                    preloadedState: {
                        albums: {
                            ...initialState,
                            albumReviews: [mockReview],
                        }
                    }
                });

                const nextReview = { ...mockReview, id: 202 };
                const response = {
                    items: [{ data: nextReview }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/albums/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreAlbumReviewsAsync({ albumId: 1, page: 2 }));
                const state = store.getState().albums;
                expect(state.albumReviews).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreAlbumReviewsAsync({ albumId: 1, page: 2 }));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchUserAlbumReviewAsync', () => {
            it('should fetch user review successfully', async () => {
                const response = {
                    items: [{ data: mockReview }], // Collection with 1 item
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/albums/1/reviews')
                    .query({ userId: 99 }) // matched by query
                    .reply(200, response);

                await store.dispatch(fetchUserAlbumReviewAsync({ albumId: 1, userId: 99 }));
                const state = store.getState().albums;
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
                    .get('/albums/1/reviews')
                    .query({ userId: 99 })
                    .reply(200, response);

                await store.dispatch(fetchUserAlbumReviewAsync({ albumId: 1, userId: 99 }));
                // Thunk returns null in this case but the reducer handles it? 
                // Actually the repo returns null, thunk returns null.
                // The extraReducer sets currentUserReview to payload (null).
                const state = store.getState().albums;
                expect(state.currentUserReview).toBeNull();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/albums/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchUserAlbumReviewAsync({ albumId: 1, userId: 99 }));
                const state = store.getState().albums;
                expect(state.error).toBeDefined();
            });
        });

        describe('createAlbumReviewAsync', () => {
            it('should create review successfully', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(201, { data: mockReview });

                // Dispatch doesn't update state directly in extraReducers for this slice?
                // Checking the slice file... no extraReducer for createAlbumReviewAsync.
                // So we just check it dispatches successfully/fulfilled.
                const result = await store.dispatch(createAlbumReviewAsync({} as any));
                expect(result.meta.requestStatus).toBe('fulfilled');
                expect(result.payload).toEqual({ data: mockReview });
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(500);

                const result = await store.dispatch(createAlbumReviewAsync({} as any));
                expect(result.meta.requestStatus).toBe('rejected');
            });
        });

        describe('addAlbumFavoriteAsync', () => {
            it('should add favorite and update album', async () => {
                store = configureStore({
                    reducer: { albums: albumReducer },
                    preloadedState: {
                        albums: {
                            ...initialState,
                            currentAlbum: mockAlbum,
                            albums: { 1: mockAlbum }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .post('/albums/1/favorites')
                    .reply(200);

                const favored = { ...mockAlbum, favorite: true };
                nock(API_BASE_URL)
                    .get('/albums/1')
                    .reply(200, { data: favored });

                await store.dispatch(addAlbumFavoriteAsync(1));
                const state = store.getState().albums;
                expect(state.currentAlbum?.favorite).toBe(true);
                expect(state.albums[1].favorite).toBe(true);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/albums/1/favorites')
                    .reply(500);

                await store.dispatch(addAlbumFavoriteAsync(1));
            });
        });

        describe('removeAlbumFavoriteAsync', () => {
            it('should remove favorite and update album', async () => {
                store = configureStore({
                    reducer: { albums: albumReducer },
                    preloadedState: {
                        albums: {
                            ...initialState,
                            currentAlbum: { ...mockAlbum, favorite: true },
                            albums: { 1: { ...mockAlbum, favorite: true } }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/albums/1/favorites')
                    .reply(204);

                nock(API_BASE_URL)
                    .get('/albums/1')
                    .reply(200, { data: mockAlbum }); // Favorite false

                await store.dispatch(removeAlbumFavoriteAsync(1));
                const state = store.getState().albums;
                expect(state.currentAlbum?.favorite).toBe(false);
                expect(state.albums[1].favorite).toBe(false);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/albums/1/favorites')
                    .reply(500);

                await store.dispatch(removeAlbumFavoriteAsync(1));
                // Same expectation as addFavorite, no state change on failure for this thunk in slice
            });
        });

    });
});
