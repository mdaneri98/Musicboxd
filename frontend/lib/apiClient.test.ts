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

            // 2. Retry with refresh token — backend detects it, returns new tokens in headers
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer valid-refresh-token')
                .reply(200, { data: 'success' }, {
                    'X-JWT-Token': 'new-access-token',
                    'X-JWT-Refresh-Token': 'new-refresh-token',
                });

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

            // 2. Retry with refresh token — backend detects it, returns new tokens in headers
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer valid-refresh-token')
                .reply(200, { data: 'success' }, {
                    'X-JWT-Token': 'new-access-token',
                    'X-JWT-Refresh-Token': 'new-refresh-token',
                });

            // Execute request
            const result = await apiClient.get(`${API_BASE_URL}/resource`);

            expect(result).toEqual({ data: 'success' });
            expect(tokenStorage.getAccessToken()).toBe('new-access-token');
            expect(tokenStorage.getRefreshToken()).toBe('new-refresh-token');
        });

        it('should queue multiple requests while refreshing', async () => {
            tokenStorage.setTokens('expired-access-token', 'valid-refresh-token');

            // Initial requests fail with 401
            nock(API_BASE_URL).get('/resource1').reply(401);
            nock(API_BASE_URL).get('/resource2').reply(401);

            // Retry of first request with refresh token — returns new tokens
            nock(API_BASE_URL)
                .get('/resource1')
                .matchHeader('Authorization', 'Bearer valid-refresh-token')
                .reply(200, { id: 1 }, {
                    'X-JWT-Token': 'new-access-token',
                    'X-JWT-Refresh-Token': 'new-refresh-token',
                });

            // Queued second request retried with new access token
            nock(API_BASE_URL)
                .get('/resource2')
                .matchHeader('Authorization', 'Bearer new-access-token')
                .reply(200, { id: 2 });

            const [res1, res2] = await Promise.all([
                apiClient.get(`${API_BASE_URL}/resource1`),
                apiClient.get(`${API_BASE_URL}/resource2`),
            ]);

            expect(res1).toEqual({ id: 1 });
            expect(res2).toEqual({ id: 2 });
        });

        it('should logout if refresh fails', async () => {
            tokenStorage.setTokens('expired-access-token', 'invalid-refresh-token');

            // Initial request fails with 401
            nock(API_BASE_URL).get('/resource').reply(401);

            // Retry with refresh token also fails
            nock(API_BASE_URL)
                .get('/resource')
                .matchHeader('Authorization', 'Bearer invalid-refresh-token')
                .reply(401);

            try {
                await apiClient.get(`${API_BASE_URL}/resource`);
                fail('Should have thrown');
            } catch (e) {
                expect(e).toBeDefined();
            }

            expect(tokenStorage.getAccessToken()).toBeNull();
            // In test env (non-production), basePath is empty, so redirect is '/login'
            expect(window.location.href).toBe('/login');
        });

        it('should redirect to login without basePath in non-production when no refresh token', async () => {
            // Only set access token, no refresh token
            tokenStorage.setAccessToken('expired-access-token');

            nock(API_BASE_URL).get('/resource').reply(401);

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
