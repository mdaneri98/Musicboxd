import type { NextConfig } from 'next';

const isProd = process.env.NODE_ENV === 'production';

const nextConfig: NextConfig = {
  reactStrictMode: true,
  output: 'export',
  trailingSlash: true,
  generateEtags: false,

  basePath: isProd ? '/paw-2024b-02' : undefined,

  images: {
    unoptimized: true,
  },

  env: {
    NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL || (isProd ? 'http://pawserver.it.itba.edu.ar/paw-2024b-02/api' : 'http://localhost:8080/api'),
  },
};

export default nextConfig;
