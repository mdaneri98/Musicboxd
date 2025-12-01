/**
 * Head Component
 * Manages document head with meta tags, title, and external resources
 * Migrated from: components/head.jsp
 */

import NextHead from 'next/head';

interface HeadProps {
  title: string;
}

const Head = ({ title }: HeadProps) => {
  return (
    <NextHead>
      <title>{title}</title>
      <meta charSet="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />

      {/* Favicon */}
      <link rel="icon" type="image/x-icon" href="/assets/logo.png" />
    </NextHead>
  );
};

export default Head;

