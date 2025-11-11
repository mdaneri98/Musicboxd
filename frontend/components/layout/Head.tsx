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

      {/* Font Awesome */}
      <link
        rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css"
      />
    </NextHead>
  );
};

export default Head;

