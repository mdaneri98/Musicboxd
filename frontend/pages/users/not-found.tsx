import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';

export default function UserNotFound() {
  const { t } = useTranslation();

  return (
    <Layout title={t('userNotFound.title')} showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">404</h1>
          <h2 className="error-title">{t('userNotFound.title')}</h2>
          <p className="error-message">
            {t('userNotFound.message')}
          </p>
          <Link href="/" className="btn btn-primary">
            {t('userNotFound.backToHome')}
          </Link>
        </div>
      </div>
    </Layout>
  );
}

