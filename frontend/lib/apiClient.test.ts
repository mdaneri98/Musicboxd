/**
 * @jest-environment node
 */
import nock from 'nock';
import { apiClient, tokenStorage } from './apiClient';

// Mock localStorage
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

Object.defineProperty(global, 'window', {
    value: {
        location: {
            href: '',
        },
    },
});

const API_BASE_URL = 'http://localhost:8080/api';

describe('ApiClient', () => {
    beforeEach(() => {
        localStorage.clear();
        nock.cleanAll();
        // Reset window location
        (window.location as any).href = '';
    });

    afterEach(() => {
        nock.cleanAll();
    });

    describe('Interceptors', () => {
        it('should refresh token and retry request on 401 error', async () => {
            // Setup initial tokens
            tokenStorage.setTokens('expired-access-token', 'valid-refresh-token');

            // 1. Initial request fails with 401
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer expired-access-token')
                .reply(401);

            // 2. Refresh token request
            nock(API_BASE_URL)
                .post('/auth/refresh', { refreshToken: 'valid-refresh-token' })
                .reply(200, {
                    access_token: 'new-access-token',
                    refresh_token: 'new-refresh-token',
                });

            // 3. Retry of initial request with new token
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer new-access-token')
                .reply(200, { data: 'success' });

            // Execute request
            const result = await apiClient.get(`${API_BASE_URL}/resource`);

            expect(result).toEqual({ data: 'success' });
            expect(tokenStorage.getAccessToken()).toBe('new-access-token');
            expect(tokenStorage.getRefreshToken()).toBe('new-refresh-token');
        });

        it('should refresh token and retry request on 403 error', async () => {
            // Setup initial tokens
            tokenStorage.setTokens('expired-access-token', 'valid-refresh-token');

            // 1. Initial request fails with 403 (Forbidden)
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer expired-access-token')
                .reply(403);

            // 2. Refresh token request
            nock(API_BASE_URL)
                .post('/auth/refresh', { refreshToken: 'valid-refresh-token' })
                .reply(200, {
                    access_token: 'new-access-token',
                    refresh_token: 'new-refresh-token',
                });

            // 3. Retry of initial request with new token
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer new-access-token')
                .reply(200, { data: 'success' });

            // Execute request
            const result = await apiClient.get(`${API_BASE_URL}/resource`);

            expect(result).toEqual({ data: 'success' });
            expect(tokenStorage.getAccessToken()).toBe('new-access-token');
            expect(tokenStorage.getRefreshToken()).toBe('new-refresh-token');
        });

        it('should queue multiple requests while refreshing', async () => {
            tokenStorage.setTokens('expired-access-token', 'valid-refresh-token');

            // Initial requests
            nock(API_BASE_URL).get('/resource1').reply(401);
            nock(API_BASE_URL).get('/resource2').reply(401);

            // Refresh request (should be called only once)
            nock(API_BASE_URL)
                .post('/auth/refresh')
                .reply(200, {
                    access_token: 'new-access-token',
                    refresh_token: 'new-refresh-token',
                });

            // Retries
            nock(API_BASE_URL).get('/resource1').matchHeader('Authorization', 'Bearer new-access-token').reply(200, { id: 1 });
            nock(API_BASE_URL).get('/resource2').matchHeader('Authorization', 'Bearer new-access-token').reply(200, { id: 2 });

            const [res1, res2] = await Promise.all([
                apiClient.get(`${API_BASE_URL}/resource1`),
                apiClient.get(`${API_BASE_URL}/resource2`),
            ]);

            expect(res1).toEqual({ id: 1 });
            expect(res2).toEqual({ id: 2 });
        });

        it('should logout if refresh fails', async () => {
            tokenStorage.setTokens('expired-access-token', 'invalid-refresh-token');

            nock(API_BASE_URL).get('/resource').reply(401);
            nock(API_BASE_URL).post('/auth/refresh').reply(400);

            try {
                await apiClient.get(`${API_BASE_URL}/resource`);
                fail('Should have thrown');
            } catch (e) {
                expect(e).toBeDefined();
            }

            expect(tokenStorage.getAccessToken()).toBeNull();
            expect(window.location.href).toBe('/login');
        });
    });
});
