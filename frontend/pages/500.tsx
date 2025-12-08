import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';

export default function Custom500() {
  const { t } = useTranslation();

  return (
    <Layout title="500 - Server Error" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">500</h1>
          <h2 className="error-title">{t('errors.pages.500.title')}</h2>
          <p className="error-message">
            {t('errors.pages.500.message')}
          </p>
          <Link href="/" className="btn btn-primary">
            {t('errors.pages.backToHome')}
          </Link>
        </div>
      </div>
    </Layout>
  );
}

