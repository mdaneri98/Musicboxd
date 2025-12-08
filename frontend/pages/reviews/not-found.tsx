import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';

export default function ReviewNotFound() {
  const { t } = useTranslation();

  return (
    <Layout title={t('reviewNotFound.title')} showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">404</h1>
          <h2 className="error-title">{t('reviewNotFound.title')}</h2>
          <p className="error-message">
            {t('reviewNotFound.message')}
          </p>
          <Link href="/" className="btn btn-primary">
            {t('reviewNotFound.backToHome')}
          </Link>
        </div>
      </div>
    </Layout>
  );
}

