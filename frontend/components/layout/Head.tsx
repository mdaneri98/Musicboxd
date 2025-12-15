import NextHead from 'next/head';
import { ASSETS } from '@/utils';

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
      <link rel="icon" type="image/x-icon" href={ASSETS.LOGO} />
    </NextHead>
  );
};

export default Head;

