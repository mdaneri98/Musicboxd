import { Html, Head, Main, NextScript } from 'next/document';
import { ASSETS } from '@/utils';

const themeInitScript = `
  (function() {
    try {
      var theme = localStorage.getItem('cached-theme') || 'dark';
      document.documentElement.setAttribute('data-theme', theme);
    } catch (e) {
      document.documentElement.setAttribute('data-theme', 'dark');
    }
  })();
`;

export default function Document() {
  return (
    <Html lang="en">
      <Head>
        <meta charSet="utf-8" />
        <meta name="description" content="Musicboxd - Share your music taste" />
        
        {/* Favicon */}
        <link rel="icon" type="image/png" href={ASSETS.LOGO} />
        <link rel="icon" href={ASSETS.LOGO} />
        
        {/* Font Awesome */}
        <link
          rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css"
        />
      </Head>
      <body>
        {/* Apply theme before hydration to prevent flash */}
        <script dangerouslySetInnerHTML={{ __html: themeInitScript }} />
        <Main />
        <NextScript />
      </body>
    </Html>
  );
}

