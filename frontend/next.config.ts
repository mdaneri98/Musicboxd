import type { NextConfig } from 'next';

const isProd = process.env.NODE_ENV === 'production';
const contextPath = isProd ? '/paw-2024b-02' : '';

const nextConfig: NextConfig = {
  reactStrictMode: true,
  output: 'export',

  basePath: contextPath,
  assetPrefix: contextPath,
  trailingSlash: true,

  images: {
    unoptimized: true,
  },

  env: {
    NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api',
  },
};

export default nextConfig;


