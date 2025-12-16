/**
 * @jest-environment node
 */
import { configureStore } from '@reduxjs/toolkit';
import nock from 'nock';
import authReducer, {
    clearError,
    setCurrentUser,
    clearAuth,
    loginAsync,
    registerAsync,
    logoutAsync,
    getCurrentUserAsync,
    checkAuthAsync,
    refreshTokenAsync,
    AuthState,
} from './authSlice';
import { User } from '@/types';

const API_BASE_URL = 'http://localhost:8080/api';

// Polyfill localStorage
const localStorageMock = (() => {
    let store: Record<string, string> = {};
    return {
        getItem: (key: string) => store[key] || null,
        setItem: (key: string, value: string) => {
            store[key] = value.toString();
        },
        removeItem: (key: string) => {
            delete store[key];
        },
        clear: () => {
            store = {};
        },
    };
})();

Object.defineProperty(global, 'localStorage', {
    value: localStorageMock,
});

// Polyfill window for tokenStorage check
Object.defineProperty(global, 'window', {
    value: {},
});

describe('authSlice', () => {
    const initialState: AuthState = {
        currentUser: null,
        jwt: { accessToken: null, refreshToken: null },
        isAuthenticated: false,
        isModerator: false,
        loading: false,
        error: null,
        initializing: true,
    };

    const mockUser: User = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        enabled: true,
        moderator: false,
        locked: false,
        image_src: 'http://example.com/avatar.jpg',
    } as any;

    describe('Reducers', () => {
        it('should handle initial state', () => {
            expect(authReducer(undefined, { type: 'unknown' })).toEqual(initialState);
        });

        it('should handle clearError', () => {
            const stateWithError = { ...initialState, error: 'Some error' };
            const actual = authReducer(stateWithError, clearError());
            expect(actual.error).toBeNull();
        });

        it('should handle setCurrentUser', () => {
            const actual = authReducer(initialState, setCurrentUser(mockUser));
            expect(actual.currentUser).toEqual(mockUser);
            expect(actual.isAuthenticated).toBe(true);
        });

        it('should handle clearAuth', () => {
            const stateWithUser = {
                ...initialState,
                currentUser: mockUser,
                isAuthenticated: true,
                jwt: { accessToken: 'token', refreshToken: 'refresh' },
            };
            const actual = authReducer(stateWithUser, clearAuth());
            expect(actual.currentUser).toBeNull();
            expect(actual.isAuthenticated).toBe(false);
            expect(actual.jwt.accessToken).toBeNull();
        });
    });

    describe('Async Thunks', () => {
        let store: ReturnType<typeof configureStore<{ auth: AuthState }>>;

        beforeEach(() => {
            store = configureStore({
                reducer: { auth: authReducer },
            });
            nock.cleanAll();
            localStorage.clear();
        });

        afterEach(() => {
            nock.cleanAll();
            localStorage.clear();
        });

        describe('loginAsync', () => {
            it('should login successfully', async () => {
                const loginResponse = {
                    user: mockUser,
                    access_token: 'access-token-123',
                    refresh_token: 'refresh-token-123',
                };

                nock(API_BASE_URL)
                    .post('/auth/login', { username: 'testuser', password: 'password' })
                    .reply(200, loginResponse);

                await store.dispatch(loginAsync({ username: 'testuser', password: 'password' }));

                const state = store.getState().auth;
                expect(state.loading).toBe(false);
                expect(state.isAuthenticated).toBe(true);
                expect(state.currentUser).toEqual(mockUser);
                expect(state.jwt.accessToken).toEqual('access-token-123');
                expect(state.jwt.refreshToken).toEqual('refresh-token-123');
            });

            it('should handle login failure', async () => {
                nock(API_BASE_URL)
                    .post('/auth/login')
                    .reply(401, { message: 'Bad credentials' });

                await store.dispatch(loginAsync({ username: 'testuser', password: 'wrong' }));

                const state = store.getState().auth;
                expect(state.loading).toBe(false);
                expect(state.isAuthenticated).toBe(false);
                // The repo/apiClient might transform the error. Usually it's the message.
                expect(state.error).toBeDefined();
            });
        });

        describe('registerAsync', () => {
            it('should register successfully', async () => {
                const registerData = {
                    username: 'newuser',
                    password: 'password',
                    email: 'new@example.com',
                    repeatPassword: 'password'
                };
                // API returns the newly created user wrapped in HAL resource usually, 
                // repo returns response.data
                const userResponse = { data: { ...mockUser, username: 'newuser' } };

                nock(API_BASE_URL)
                    .post('/auth/register')
                    .reply(201, userResponse);

                await store.dispatch(registerAsync(registerData));

                const state = store.getState().auth;
                expect(state.loading).toBe(false);
                expect(state.error).toBeNull();
                // Register usually doesn't log the user in automatically in this slice logic (based on extraReducers)
                // It just says "loading = false, error = null".
                expect(state.isAuthenticated).toBe(false);
            });

            it('should handle register failure', async () => {
                nock(API_BASE_URL)
                    .post('/auth/register')
                    .reply(400, { message: 'Username taken' });

                await store.dispatch(registerAsync({ username: 'taken', password: 'p', email: 'e', repeatPassword: 'p' }));

                const state = store.getState().auth;
                expect(state.error).toBeDefined();
            });
        });

        describe('logoutAsync', () => {
            it('should logout successfully', async () => {
                // Preload
                localStorage.setItem('access_token', 'old');
                localStorage.setItem('refresh_token', 'old-refresh');

                // Using preloadedState to simulate logged in
                store = configureStore({
                    reducer: { auth: authReducer },
                    preloadedState: {
                        auth: {
                            ...initialState,
                            isAuthenticated: true,
                            currentUser: mockUser,
                            jwt: { accessToken: 'old', refreshToken: 'old-refresh' },
                        }
                    }
                });

                // The slice calls authRepository.logout() which calls POST /auth/logout with refresh token
                nock(API_BASE_URL)
                    .post('/auth/logout', { refreshToken: 'old-refresh' })
                    .reply(204);

                await store.dispatch(logoutAsync());

                const state = store.getState().auth;
                expect(state.isAuthenticated).toBe(false);
                expect(state.currentUser).toBeNull();
                expect(state.jwt.accessToken).toBeNull();
            });
        });

        describe('getCurrentUserAsync', () => {
            it('should fetch current user successfully', async () => {
                nock(API_BASE_URL)
                    .get('/auth/me')
                    .reply(200, { data: mockUser });

                await store.dispatch(getCurrentUserAsync());

                const state = store.getState().auth;
                expect(state.currentUser).toEqual(mockUser);
                expect(state.isAuthenticated).toBe(true);
            });

            it('should handle failure', async () => {
                nock(API_BASE_URL)
                    .get('/auth/me')
                    .reply(404);

                await store.dispatch(getCurrentUserAsync());
                const state = store.getState().auth;
                expect(state.error).toBeDefined();
                expect(state.isAuthenticated).toBe(false);
            });
        });

        describe('checkAuthAsync', () => {
            it('should restore checking auth if tokens exist and valid', async () => {
                // Setup localStorage
                localStorage.setItem('access_token', 'valid-access');
                localStorage.setItem('refresh_token', 'valid-refresh');

                nock(API_BASE_URL)
                    .get('/auth/me')
                    .reply(200, { data: mockUser });

                await store.dispatch(checkAuthAsync());

                const state = store.getState().auth;
                expect(state.initializing).toBe(false);
                expect(state.isAuthenticated).toBe(true);
                expect(state.currentUser).toEqual(mockUser);
                expect(state.jwt.accessToken).toBe('valid-access');
            });

            it('should fail if no tokens', async () => {
                // No tokens in localStorage
                await store.dispatch(checkAuthAsync());

                const state = store.getState().auth;
                expect(state.initializing).toBe(false);
                expect(state.isAuthenticated).toBe(false);
                expect(state.currentUser).toBeNull();
            });

            it('should fail if user fetch fails (invalid token)', async () => {
                localStorage.setItem('access_token', 'bad-token');
                localStorage.setItem('refresh_token', 'bad-refresh');

                nock(API_BASE_URL)
                    .get('/auth/me')
                    .reply(500);

                await store.dispatch(checkAuthAsync());

                const state = store.getState().auth;
                expect(state.initializing).toBe(false);
                expect(state.isAuthenticated).toBe(false);
                // Should also clear auth
                expect(localStorage.getItem('access_token')).toBeNull();
            });
        });

        describe('refreshTokenAsync', () => {
            it('should refresh token successfully', async () => {
                localStorage.setItem('refresh_token', 'valid-refresh');

                nock(API_BASE_URL)
                    .post('/auth/refresh', { refreshToken: 'valid-refresh' })
                    .reply(200, {
                        access_token: 'new-access',
                        refresh_token: 'new-refresh'
                    });

                await store.dispatch(refreshTokenAsync());

                const state = store.getState().auth;
                expect(state.jwt.accessToken).toBe('new-access');
                expect(state.jwt.refreshToken).toBe('new-refresh');
            });

            it('should fail if no refresh token', async () => {
                await store.dispatch(refreshTokenAsync());
                const state = store.getState().auth;
                expect(state.error).toContain('No refresh token available');
            });

            it('should handle refresh API failure', async () => {
                localStorage.setItem('refresh_token', 'bad-refresh');
                nock(API_BASE_URL)
                    .post('/auth/refresh')
                    .reply(400); // Invalid token

                await store.dispatch(refreshTokenAsync());
                const state = store.getState().auth;
                expect(state.error).toBeDefined();
                expect(state.isAuthenticated).toBe(false);
            });
        });

    });
});
