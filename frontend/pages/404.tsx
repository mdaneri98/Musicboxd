import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';

export default function Custom404() {
  const { t } = useTranslation();

  return (
    <Layout title="404 - Not Found" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">404</h1>
          <h2 className="error-title">{t('errors.pages.404.title')}</h2>
          <p className="error-message">
            {t('errors.pages.404.message')}
          </p>
          <Link href="/" className="btn btn-primary">
            {t('errors.pages.backToHome')}
          </Link>
        </div>
      </div>
    </Layout>
  );
}

