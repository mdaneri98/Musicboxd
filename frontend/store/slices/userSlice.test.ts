/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import userReducer, {
    clearError,
    clearCurrentProfile,
    addUser,
    removeUser,
    fetchUsersAsync,
    fetchUserByIdAsync,
    updateUserAsync,
    deleteUserAsync,
    fetchFollowersAsync,
    fetchMoreFollowersAsync,
    fetchFollowingAsync,
    fetchMoreFollowingAsync,
    followUserAsync,
    unfollowUserAsync,
    fetchFavoriteArtistsAsync,
    fetchFavoriteAlbumsAsync,
    fetchFavoriteSongsAsync,
    fetchUserReviewsAsync,
    fetchMoreUserReviewsAsync,
    updateUserConfigAsync,
    UserState,
} from './userSlice';
import { User, Artist, Album, Song, Review } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('userSlice', () => {
    const initialState: UserState = {
        users: {},
        currentProfile: null,
        followers: [],
        following: [],
        favoriteArtists: [],
        favoriteAlbums: [],
        favoriteSongs: [],
        userReviews: [],
        pagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        followersPagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        followingPagination: {
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
        loadingProfile: false,
        loadingFollowers: false,
        loadingMoreFollowers: false,
        loadingFollowing: false,
        loadingMoreFollowing: false,
        loadingFavorites: false,
        loadingFavoriteArtists: false,
        loadingFavoriteAlbums: false,
        loadingFavoriteSongs: false,
        loadingReviews: false,
        loadingMoreReviews: false,
        error: null,
    };

    const mockUser: User = {
        id: 1,
        username: 'TestUser',
        email: 'test@example.com',
        followers_amount: 0,
        following_amount: 0,
        role: 'USER',
        enabled: true,
        nonLocked: true,
    } as any;

    const mockArtist: Artist = {
        id: 1,
        name: 'Artist',
    } as any;

    const mockAlbum: Album = {
        id: 101,
        name: 'Album',
    } as any;

    const mockSong: Song = {
        id: 201,
        name: 'Song',
    } as any;

    const mockReview: Review = {
        id: 301,
        title: 'Review',
        user: { id: 1, username: 'TestUser' },
    } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(userReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const state = { ...initialState, error: 'Error' };
            const actual = userReducer(state, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle clearCurrentProfile', () => {
            const state: UserState = {
                ...initialState,
                currentProfile: mockUser,
                followers: [mockUser],
                following: [mockUser],
                favoriteArtists: [mockArtist],
                favoriteAlbums: [mockAlbum],
                favoriteSongs: [mockSong],
                userReviews: [mockReview],
            };
            const actual = userReducer(state, clearCurrentProfile());
            expect(actual.currentProfile).toBeNull();
            expect(actual.followers).toEqual([]);
            expect(actual.following).toEqual([]);
            expect(actual.favoriteArtists).toEqual([]);
            expect(actual.favoriteAlbums).toEqual([]);
            expect(actual.favoriteSongs).toEqual([]);
            expect(actual.userReviews).toEqual([]);
        });

        it('should handle addUser', () => {
            const actual = userReducer(initialState, addUser(mockUser));
            expect(actual.users[1]).toEqual(mockUser);
        });

        it('should handle removeUser', () => {
            const state = {
                ...initialState,
                users: { 1: mockUser },
            };
            const actual = userReducer(state, removeUser(1));
            expect(actual.users[1]).toBeUndefined();
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ users: UserState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { users: userReducer },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('fetchUsersAsync', () => {
            it('should fetch users successfully', async () => {
                const response = {
                    items: [{ data: mockUser }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchUsersAsync({}));
                const state = store.getState().users;
                expect(state.loading).toBe(false);
                expect(state.users[1]).toEqual(mockUser);
            });

            it('should handle fetch failure', async () => {
                nock(API_BASE_URL)
                    .get('/users')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchUsersAsync({}));
                const state = store.getState().users;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchUserByIdAsync', () => {
            it('should fetch user successfully', async () => {
                nock(API_BASE_URL)
                    .get('/users/1')
                    .reply(200, { data: mockUser });

                await store.dispatch(fetchUserByIdAsync(1));
                const state = store.getState().users;
                expect(state.currentProfile).toEqual(mockUser);
                expect(state.users[1]).toEqual(mockUser);
            });
        });

        describe('updateUserAsync', () => {
            it('should update user successfully', async () => {
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            users: { 1: mockUser },
                            currentProfile: mockUser,
                        }
                    }
                });

                const updated = { ...mockUser, username: 'Updated' };
                nock(API_BASE_URL)
                    .put('/users/1')
                    .reply(200, { data: updated });

                await store.dispatch(updateUserAsync({ id: 1, userData: {} as any }));
                const state = store.getState().users;
                expect(state.users[1].username).toBe('Updated');
                expect(state.currentProfile?.username).toBe('Updated');
            });
        });

        describe('deleteUserAsync', () => {
            it('should delete user successfully', async () => {
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            users: { 1: mockUser },
                            currentProfile: mockUser,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/users/1')
                    .reply(204);

                await store.dispatch(deleteUserAsync(1));
                const state = store.getState().users;
                expect(state.users[1]).toBeUndefined();
                expect(state.currentProfile).toBeNull();
            });
        });

        describe('fetchFollowersAsync', () => {
            it('should fetch followers successfully', async () => {
                const response = {
                    items: [{ data: mockUser }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users/1/followers')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchFollowersAsync({ userId: 1 }));
                const state = store.getState().users;
                expect(state.followers).toHaveLength(1);
            });
        });

        describe('fetchMoreFollowersAsync', () => {
            it('should append followers successfully', async () => {
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            followers: [mockUser],
                        }
                    }
                });

                const nextFollower = { ...mockUser, id: 2 };
                const response = {
                    items: [{ data: nextFollower }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/users/1/followers')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreFollowersAsync({ userId: 1, page: 2 }));
                const state = store.getState().users;
                expect(state.followers).toHaveLength(2);
            });
        });

        describe('fetchFollowingAsync', () => {
            it('should fetch following successfully', async () => {
                const response = {
                    items: [{ data: mockUser }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users/1/followings')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchFollowingAsync({ userId: 1 }));
                const state = store.getState().users;
                expect(state.following).toHaveLength(1);
            });
        });

        describe('fetchMoreFollowingAsync', () => {
            it('should append following successfully', async () => {
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            following: [mockUser],
                        }
                    }
                });

                const nextFollowing = { ...mockUser, id: 2 };
                const response = {
                    items: [{ data: nextFollowing }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/users/1/followings')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreFollowingAsync({ userId: 1, page: 2 }));
                const state = store.getState().users;
                expect(state.following).toHaveLength(2);
            });
        });

        describe('followUserAsync', () => {
            it('should follow user and update count', async () => {
                const targetUser = { ...mockUser, followers_amount: 0 };
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            users: { 1: targetUser },
                            currentProfile: targetUser,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .post('/users/1/followers')
                    .reply(200);

                await store.dispatch(followUserAsync(1));
                const state = store.getState().users;
                expect(state.currentProfile?.followers_amount).toBe(1);
                expect(state.users[1].followers_amount).toBe(1);
            });
        });

        describe('unfollowUserAsync', () => {
            it('should unfollow user and update count', async () => {
                const targetUser = { ...mockUser, followers_amount: 1 };
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            users: { 1: targetUser },
                            currentProfile: targetUser,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/users/1/followers')
                    .reply(204);

                await store.dispatch(unfollowUserAsync(1));
                const state = store.getState().users;
                expect(state.currentProfile?.followers_amount).toBe(0);
                expect(state.users[1].followers_amount).toBe(0);
            });
        });

        describe('fetchFavoriteArtistsAsync', () => {
            it('should fetch favorite artists successfully', async () => {
                const response = {
                    items: [{ data: mockArtist }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users/1/favorites/artists')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchFavoriteArtistsAsync(1));
                const state = store.getState().users;
                expect(state.favoriteArtists).toHaveLength(1);
            });
        });

        describe('fetchFavoriteAlbumsAsync', () => {
            it('should fetch favorite albums successfully', async () => {
                const response = {
                    items: [{ data: mockAlbum }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users/1/favorites/albums')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchFavoriteAlbumsAsync(1));
                const state = store.getState().users;
                expect(state.favoriteAlbums).toHaveLength(1);
            });
        });

        describe('fetchFavoriteSongsAsync', () => {
            it('should fetch favorite songs successfully', async () => {
                const response = {
                    items: [{ data: mockSong }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users/1/favorites/songs')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchFavoriteSongsAsync(1));
                const state = store.getState().users;
                expect(state.favoriteSongs).toHaveLength(1);
            });
        });

        describe('fetchUserReviewsAsync', () => {
            it('should fetch user reviews successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/users/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchUserReviewsAsync({ userId: 1 }));
                const state = store.getState().users;
                expect(state.userReviews).toHaveLength(1);
            });
        });

        describe('fetchMoreUserReviewsAsync', () => {
            it('should append user reviews successfully', async () => {
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            userReviews: [mockReview],
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
                    .get('/users/1/reviews')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreUserReviewsAsync({ userId: 1, page: 2 }));
                const state = store.getState().users;
                expect(state.userReviews).toHaveLength(2);
            });
        });

        describe('updateUserConfigAsync', () => {
            it('should update user config successfully', async () => {
                store = configureStore({
                    reducer: { users: userReducer },
                    preloadedState: {
                        users: {
                            ...initialState,
                            users: { 1: mockUser },
                            currentProfile: mockUser,
                        }
                    }
                });

                const updated = { ...mockUser, username: 'ConfigUpdated' };
                nock(API_BASE_URL)
                    .patch('/users/1')
                    .reply(200, { data: updated });

                await store.dispatch(updateUserConfigAsync({ userId: 1, userData: {} as any }));
                const state = store.getState().users;
                expect(state.users[1].username).toBe('ConfigUpdated');
                expect(state.currentProfile?.username).toBe('ConfigUpdated');
            });
        });

    });
});
