// Learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';

Object.assign(global, { TextEncoder, TextDecoder });

if (typeof global.Response === 'undefined') {
    global.Response = class Response { } as any;
    global.Request = class Request { } as any;
    global.Headers = class Headers { } as any;
}

// Global mock for react-i18next
jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (str: string) => str,
        i18n: {
            changeLanguage: () => new Promise(() => { }),
            language: 'en',
        },
    }),
    initReactI18next: {
        type: '3rdParty',
        init: () => { },
    },
}));
