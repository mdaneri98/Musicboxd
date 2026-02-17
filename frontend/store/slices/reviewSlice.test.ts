/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import reviewReducer, {
    clearError,
    clearCurrentReview,
    clearReviews,
    addReview,
    removeReview,
    fetchReviewsAsync,
    fetchMoreReviewsAsync,
    fetchReviewByIdAsync,
    createReviewAsync,
    updateReviewAsync,
    deleteReviewAsync,
    fetchReviewCommentsAsync,
    fetchMoreReviewCommentsAsync,
    postCommentAsync,
    deleteCommentAsync,
    fetchReviewLikesAsync,
    fetchMoreReviewLikesAsync,
    likeReviewAsync,
    unlikeReviewAsync,
    ReviewState,
} from './reviewSlice';
import { Review, Comment, User } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

describe('reviewSlice', () => {
    const initialState: ReviewState = {
        reviews: {},
        orderedReviewsIds: [],
        currentReview: null,
        reviewComments: [],
        reviewLikes: [],
        pagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        commentsPagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        likesPagination: {
            page: 1,
            size: 10,
            totalCount: 0,
            hasMore: true,
        },
        loading: false,
        loadingMore: false,
        loadingReview: false,
        loadingComments: false,
        loadingMoreComments: false,
        loadingLikes: false,
        loadingMoreLikes: false,
        error: null,
    };

    const mockReview: Review = {
        id: 1,
        title: 'Review 1',
        description: 'Desc',
        score: 5,
        user: { id: 1, username: 'User 1' } as any,
        likes: 0,
        liked: false,
        comment_amount: 0,
        blocked: false,
    } as any;

    const mockComment: Comment = {
        id: 101,
        review_id: 1,
        description: 'Comment 1',
        user: { id: 2, username: 'User 2' } as any,
    } as any;

    const mockUser: User = {
        id: 10,
        username: 'Liker',
    } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(reviewReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const state = { ...initialState, error: 'Error' };
            const actual = reviewReducer(state, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle clearCurrentReview', () => {
            const state: ReviewState = {
                ...initialState,
                currentReview: mockReview,
                reviewComments: [mockComment],
                reviewLikes: [mockUser],
            };
            const actual = reviewReducer(state, clearCurrentReview());
            expect(actual.currentReview).toBeNull();
            expect(actual.reviewComments).toEqual([]);
            expect(actual.reviewLikes).toEqual([]);
        });

        it('should handle clearReviews', () => {
            const state: ReviewState = {
                ...initialState,
                reviews: { 1: mockReview },
                orderedReviewsIds: [1],
                pagination: { ...initialState.pagination, page: 2 },
            };
            const actual = reviewReducer(state, clearReviews());
            expect(actual.reviews).toEqual({});
            expect(actual.orderedReviewsIds).toEqual([]);
            expect(actual.pagination.page).toBe(1);
        });

        it('should handle addReview', () => {
            const actual = reviewReducer(initialState, addReview(mockReview));
            expect(actual.reviews[1]).toEqual(mockReview);
        });

        it('should handle removeReview', () => {
            const state = {
                ...initialState,
                reviews: { 1: mockReview },
            };
            const actual = reviewReducer(state, removeReview(1));
            expect(actual.reviews[1]).toBeUndefined();
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ reviews: ReviewState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { reviews: reviewReducer },
            });
            nock.cleanAll();
        });

        afterEach(() => {
            nock.cleanAll();
        });

        describe('fetchReviewsAsync', () => {
            it('should fetch reviews successfully', async () => {
                const response = {
                    items: [{ data: mockReview }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/reviews')
                    .query(true) // match any query params
                    .reply(200, response);

                await store.dispatch(fetchReviewsAsync({}));
                const state = store.getState().reviews;
                expect(state.loading).toBe(false);
                expect(state.reviews[1]).toEqual(mockReview);
                expect(state.orderedReviewsIds).toContain(1);
            });

            it('should handle fetch failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchReviewsAsync({}));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreReviewsAsync', () => {
            it('should append reviews successfully', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            reviews: { 1: mockReview },
                            orderedReviewsIds: [1],
                        }
                    }
                });

                const nextReview = { ...mockReview, id: 2 };
                const response = {
                    items: [{ data: nextReview }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/reviews')
                    .query({ page: 2, size: 10 })
                    .reply(200, response);

                await store.dispatch(fetchMoreReviewsAsync({ page: 2 }));
                const state = store.getState().reviews;
                expect(state.loadingMore).toBe(false);
                expect(state.reviews[2]).toBeDefined();
                expect(state.orderedReviewsIds).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreReviewsAsync({ page: 2 }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchReviewByIdAsync', () => {
            it('should fetch review successfully', async () => {
                nock(API_BASE_URL)
                    .get('/reviews/1')
                    .reply(200, { data: mockReview });

                await store.dispatch(fetchReviewByIdAsync(1));
                const state = store.getState().reviews;
                expect(state.currentReview).toEqual(mockReview);
                expect(state.reviews[1]).toEqual(mockReview);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews/1')
                    .reply(500);

                await store.dispatch(fetchReviewByIdAsync(1));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('createReviewAsync', () => {
            it('should create review successfully', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(201, { data: mockReview });

                await store.dispatch(createReviewAsync({} as any));
                const state = store.getState().reviews;
                expect(state.reviews[1]).toEqual(mockReview);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/reviews')
                    .reply(500);

                await store.dispatch(createReviewAsync({} as any));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('updateReviewAsync', () => {
            it('should update review successfully', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            reviews: { 1: mockReview },
                            currentReview: mockReview,
                        }
                    }
                });

                const updated = { ...mockReview, title: 'Updated' };
                nock(API_BASE_URL)
                    .put('/reviews/1')
                    .reply(200, { data: updated });

                await store.dispatch(updateReviewAsync({ id: 1, reviewData: {} as any }));
                const state = store.getState().reviews;
                expect(state.reviews[1].title).toBe('Updated');
                expect(state.currentReview?.title).toBe('Updated');
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .put('/reviews/1')
                    .reply(500);

                await store.dispatch(updateReviewAsync({ id: 1, reviewData: {} as any }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('deleteReviewAsync', () => {
            it('should delete review successfully', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            reviews: { 1: mockReview },
                            currentReview: mockReview,
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/reviews/1')
                    .reply(204);

                await store.dispatch(deleteReviewAsync(1));
                const state = store.getState().reviews;
                expect(state.reviews[1]).toBeUndefined();
                expect(state.currentReview).toBeNull();
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/reviews/1')
                    .reply(500);

                await store.dispatch(deleteReviewAsync(1));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchReviewLikesAsync', () => {
            it('should fetch likes successfully', async () => {
                const response = {
                    items: [{ data: mockUser }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/reviews/1/likes')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchReviewLikesAsync({ reviewId: 1 }));
                const state = store.getState().reviews;
                expect(state.reviewLikes).toHaveLength(1);
                expect(state.reviewLikes[0]).toEqual(mockUser);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews/1/likes')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchReviewLikesAsync({ reviewId: 1 }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreReviewLikesAsync', () => {
            it('should append likes successfully', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            reviewLikes: [mockUser],
                        }
                    }
                });

                const nextUser = { ...mockUser, id: 11 };
                const response = {
                    items: [{ data: nextUser }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/reviews/1/likes')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreReviewLikesAsync({ reviewId: 1, page: 2 }));
                const state = store.getState().reviews;
                expect(state.reviewLikes).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews/1/likes')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreReviewLikesAsync({ reviewId: 1, page: 2 }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('likeReviewAsync', () => {
            it('should like review and update count', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            currentReview: mockReview,
                            reviews: { 1: mockReview }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .post('/reviews/1/likes')
                    .reply(204);

                await store.dispatch(likeReviewAsync(1));
                const state = store.getState().reviews;
                expect(state.currentReview?.liked).toBe(true);
                expect(state.currentReview?.likes).toBe(1);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/reviews/1/likes')
                    .reply(500);

                await store.dispatch(likeReviewAsync(1));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('unlikeReviewAsync', () => {
            it('should unlike review and update count', async () => {
                const likedReview = { ...mockReview, liked: true, likes: 1 };
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            currentReview: likedReview,
                            reviews: { 1: likedReview }
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/reviews/1/likes')
                    .reply(204);

                await store.dispatch(unlikeReviewAsync({ reviewId: 1, userId: 10 }));
                const state = store.getState().reviews;
                expect(state.currentReview?.liked).toBe(false);
                expect(state.currentReview?.likes).toBe(0);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/reviews/1/likes')
                    .reply(500);

                await store.dispatch(unlikeReviewAsync({ reviewId: 1, userId: 10 }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchReviewCommentsAsync', () => {
            it('should fetch comments successfully', async () => {
                const response = {
                    items: [{ data: mockComment }],
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 1,
                };

                nock(API_BASE_URL)
                    .get('/reviews/1/comments')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchReviewCommentsAsync({ reviewId: 1 }));
                const state = store.getState().reviews;
                expect(state.reviewComments).toHaveLength(1);
                expect(state.reviewComments[0]).toEqual(mockComment);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews/1/comments')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchReviewCommentsAsync({ reviewId: 1 }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('fetchMoreReviewCommentsAsync', () => {
            it('should append comments successfully', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            reviewComments: [mockComment],
                        }
                    }
                });

                const nextComment = { ...mockComment, id: 102 };
                const response = {
                    items: [{ data: nextComment }],
                    currentPage: 2,
                    pageSize: 10,
                    totalCount: 2,
                };

                nock(API_BASE_URL)
                    .get('/reviews/1/comments')
                    .query(true)
                    .reply(200, response);

                await store.dispatch(fetchMoreReviewCommentsAsync({ reviewId: 1, page: 2 }));
                const state = store.getState().reviews;
                expect(state.reviewComments).toHaveLength(2);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/reviews/1/comments')
                    .query(true)
                    .reply(500);

                await store.dispatch(fetchMoreReviewCommentsAsync({ reviewId: 1, page: 2 }));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('postCommentAsync', () => {
            it('should create comment and update review count', async () => {
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            currentReview: mockReview,
                            reviews: { 1: mockReview } // ID 1
                        }
                    }
                });

                nock(API_BASE_URL)
                    .post('/comments')
                    .reply(201, { data: mockComment }); // review_id = 1

                await store.dispatch(postCommentAsync({} as any));
                const state = store.getState().reviews;
                expect(state.reviewComments).toHaveLength(1);
                expect(state.reviews[1].comment_amount).toBe(1);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .post('/comments')
                    .reply(500);

                await store.dispatch(postCommentAsync({} as any));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

        describe('deleteCommentAsync', () => {
            it('should delete comment and update review count', async () => {
                const reviewWithComments = { ...mockReview, comment_amount: 1 };
                store = configureStore({
                    reducer: { reviews: reviewReducer },
                    preloadedState: {
                        reviews: {
                            ...initialState,
                            currentReview: reviewWithComments,
                            reviews: { 1: reviewWithComments },
                            reviewComments: [mockComment] // ID 101, review_id 1
                        }
                    }
                });

                nock(API_BASE_URL)
                    .delete('/comments/101')
                    .reply(204);

                await store.dispatch(deleteCommentAsync(101));
                const state = store.getState().reviews;
                expect(state.reviewComments).toHaveLength(0);
                expect(state.reviews[1].comment_amount).toBe(0);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .delete('/comments/101')
                    .reply(500);

                await store.dispatch(deleteCommentAsync(101));
                const state = store.getState().reviews;
                expect(state.error).toBeDefined();
            });
        });

    });
});
