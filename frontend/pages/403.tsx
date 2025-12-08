import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';

export default function Custom403() {
  const { t } = useTranslation();

  return (
    <Layout title="403 - Forbidden" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">403</h1>
          <h2 className="error-title">{t('errors.pages.403.title')}</h2>
          <p className="error-message">
            {t('errors.pages.403.message')}
          </p>
          <Link href="/" className="btn btn-primary">
            {t('errors.pages.backToHome')}
          </Link>
        </div>
      </div>
    </Layout>
  );
}

