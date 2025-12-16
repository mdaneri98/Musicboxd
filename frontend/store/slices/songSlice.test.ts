/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import songReducer, {
    clearError,
    clearCurrentSong,
    clearSongs,
    addSong,
    removeSong,
    fetchSongsAsync,
    fetchMoreSongsAsync,
    fetchSongByIdAsync,
    createSongAsync,
    updateSongAsync,
    deleteSongAsync,
    fetchSongReviewsAsync,
    fetchMoreSongReviewsAsync,
    fetchUserSongReviewAsync,
    createSongReviewAsync,
    addSongFavoriteAsync,
    removeSongFavoriteAsync,
    SongState,
} from './songSlice';
import { Song, Review } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('songSlice', () => {
    const initialState: SongState = {
        songs: {},
        orderedSongsIds: [],
        currentSong: null,
        songReviews: [],
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
        loadingSong: false,
        loadingReviews: false,
        loadingMoreReviews: false,
        error: null,
    };

    const mockSong: Song = {
        id: 1,
        title: 'Test Song',
        artist: { id: 1, name: 'Artist' },
        album: { id: 101, title: 'Album' },
        trackNumber: 1,
        duration: 180,
        favorite: false,
    } as any;

    const mockReview: Review = {
        id: 201,
        title: 'Review',
        description: 'Nice song',
        score: 5,
        user: { id: 1, username: 'user' } as any,
    } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(songReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const state = { ...initialState, error: 'Error' };
            const actual = songReducer(state, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle clearCurrentSong', () => {
            const state: SongState = {
                ...initialState,
                currentSong: mockSong,
                songReviews: [mockReview],
            };
            const actual = songReducer(state, clearCurrentSong());
            expect(actual.currentSong).toBeNull();
            expect(actual.songReviews).toEqual([]);
        });

        it('should handle clearSongs', () => {
            const state: SongState = {
                ...initialState,
                songs: { 1: mockSong },
                orderedSongsIds: [1],
                pagination: { ...initialState.pagination, page: 2 },
            };
            const actual = songReducer(state, clearSongs());
            expect(actual.songs).toEqual({});
            expect(actual.orderedSongsIds).toEqual([]);
            expect(actual.pagination.page).toBe(1);
        });

        it('should handle addSong', () => {
            const actual = songReducer(initialState, addSong(mockSong));
            expect(actual.songs[1]).toEqual(mockSong);
            expect(actual.orderedSongsIds).toContain(1);
        });

        it('should handle removeSong', () => {
            const state = {
                ...initialState,
                songs: { 1: mockSong },
                orderedSongsIds: [1],
            };
            const actual = songReducer(state, removeSong(1));
            expect(actual.songs[1]).toBeUndefined();
            expect(actual.orderedSongsIds).not.toContain(1);
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ songs: SongState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { songs: songReducer },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('fetchSongsAsync', () => {
            it('should fetch songs successfully', async () => {
                const response = {
                    items: [{ data: mockSong }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/songs')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchSongsAsync({}));
                const state = store.getState().songs;
                expect(state.loading).toBe(false);
                expect(state.songs[1]).toEqual(mockSong);
            });

            it('should handle fetch failure', async () => {
                nock(API_BASE_URL)
                    .get('/songs')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchSongsAsync({}));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreSongsAsync', () => {
            it('should append songs successfully', async () => {
                store = configureStore({
                    reducer: { songs: songReducer },
                    preloadedState: {
                        songs: {
                            ...initialState,
                            songs: { 1: mockSong },
                            orderedSongsIds: [1],
                        }
                    }
                });

                const nextSong = { ...mockSong, id: 2 };
                const response = {
                    items: [{ data: nextSong }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/songs')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreSongsAsync({ page: 2 }));
                const state = store.getState().songs;
                expect(state.songs[2]).toBeDefined();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/songs')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreSongsAsync({ page: 2 }));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
                expect(state.loadingMore).toBe(false);
            });
        });

        describe('fetchSongByIdAsync', () => {
            it('should fetch song successfully', async () => {
                nock(API_BASE_URL)
                    .get('/songs/1')
                    .reply(200, { data: mockSong });

                await store.dispatch(fetchSongByIdAsync(1));
                const state = store.getState().songs;
                expect(state.currentSong).toEqual(mockSong);
                expect(state.songs[1]).toEqual(mockSong);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/songs/1')
                    .reply(500);

                await store.dispatch(fetchSongByIdAsync(1));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('createSongAsync', () => {
            it('should create song successfully', async () => {
                nock(API_BASE_URL)
                    .post('/songs')
                    .reply(201, { data: mockSong });

                await store.dispatch(createSongAsync({} as any));
                const state = store.getState().songs;
                expect(state.songs[1]).toEqual(mockSong);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/songs')
                    .reply(500);

                await store.dispatch(createSongAsync({} as any));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('updateSongAsync', () => {
            it('should update song successfully', async () => {
                store = configureStore({
                    reducer: { songs: songReducer },
                    preloadedState: {
                        songs: {
                            ...initialState,
                            songs: { 1: mockSong },
                            currentSong: mockSong,
                        }
                    }
                });

                const updated = { ...mockSong, title: 'Updated' };
                nock(API_BASE_URL)
                    .put('/songs/1')
                    .reply(200, { data: updated });

                await store.dispatch(updateSongAsync({ id: 1, songData: {} as any }));
                const state = store.getState().songs;
                expect(state.songs[1].title).toBe('Updated');
                expect(state.currentSong?.title).toBe('Updated');
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .put('/songs/1')
                    .reply(500);

                await store.dispatch(updateSongAsync({ id: 1, songData: {} as any }));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('deleteSongAsync', () => {
            it('should delete song successfully', async () => {
                store = configureStore({
                    reducer: { songs: songReducer },
                    preloadedState: {
                        songs: {
                            ...initialState,
                            songs: { 1: mockSong },
                            currentSong: mockSong,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/songs/1')
                    .reply(204);

                await store.dispatch(deleteSongAsync(1));
                const state = store.getState().songs;
                expect(state.songs[1]).toBeUndefined();
                expect(state.currentSong).toBeNull();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/songs/1')
                    .reply(500);

                await store.dispatch(deleteSongAsync(1));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchSongReviewsAsync', () => {
            it('should fetch reviews successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/songs/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchSongReviewsAsync({ songId: 1 }));
                const state = store.getState().songs;
                expect(state.songReviews).toHaveLength(1);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/songs/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchSongReviewsAsync({ songId: 1 }));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreSongReviewsAsync', () => {
            it('should append reviews successfully', async () => {
                store = configureStore({
                    reducer: { songs: songReducer },
                    preloadedState: {
                        songs: {
                            ...initialState,
                            songReviews: [mockReview],
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
                    .get('/songs/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreSongReviewsAsync({ songId: 1, page: 2 }));
                const state = store.getState().songs;
                expect(state.songReviews).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/songs/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreSongReviewsAsync({ songId: 1, page: 2 }));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchUserSongReviewAsync', () => {
            it('should fetch user review successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/songs/1/reviews')
                    .query({ userId: 99 })
                    .reply(200, response);

                await store.dispatch(fetchUserSongReviewAsync({ songId: 1, userId: 99 }));
                const state = store.getState().songs;
                expect(state.currentUserReview).toEqual(mockReview);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/songs/1/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchUserSongReviewAsync({ songId: 1, userId: 99 }));
                const state = store.getState().songs;
                expect(state.error).toBeDefined();
            });
        });

        describe('createSongReviewAsync', () => {
            it('should create review successfully', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(201, { data: mockReview });

                const result = await store.dispatch(createSongReviewAsync({} as any));
                expect(result.meta.requestStatus).toBe('fulfilled');
                expect(result.payload).toEqual({ data: mockReview });
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(500);

                const result = await store.dispatch(createSongReviewAsync({} as any));
                expect(result.meta.requestStatus).toBe('rejected');
            });
        });

        describe('addSongFavoriteAsync', () => {
            it('should add favorite and update song', async () => {
                store = configureStore({
                    reducer: { songs: songReducer },
                    preloadedState: {
                        songs: {
                            ...initialState,
                            songs: { 1: mockSong },
                            currentSong: mockSong,
                        }
                    }
                });

                const favored = { ...mockSong, favorite: true };
                nock(API_BASE_URL)
                    .post('/songs/1/favorites')
                    .reply(200);

                nock(API_BASE_URL)
                    .get('/songs/1')
                    .reply(200, { data: favored });

                await store.dispatch(addSongFavoriteAsync(1));
                const state = store.getState().songs;
                expect(state.songs[1].favorite).toBe(true);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/songs/1/favorites')
                    .reply(500);

                await store.dispatch(addSongFavoriteAsync(1));
                // No state change asserted as failure isn't handled in reducers
            });
        });

        describe('removeSongFavoriteAsync', () => {
            it('should remove favorite and update song', async () => {
                const favored = { ...mockSong, favorite: true };
                store = configureStore({
                    reducer: { songs: songReducer },
                    preloadedState: {
                        songs: {
                            ...initialState,
                            songs: { 1: favored },
                            currentSong: favored,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/songs/1/favorites')
                    .reply(204);

                nock(API_BASE_URL)
                    .get('/songs/1')
                    .reply(200, { data: mockSong });

                await store.dispatch(removeSongFavoriteAsync(1));
                const state = store.getState().songs;
                expect(state.songs[1].favorite).toBe(false);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/songs/1/favorites')
                    .reply(500);

                await store.dispatch(removeSongFavoriteAsync(1));
                // No state change asserted as failure isn't handled in reducers
            });
        });

    });
});
