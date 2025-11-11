import Link from 'next/link';
import { Layout } from '@/components/layout';

export default function Custom404() {
  return (
    <Layout title="404 - Not Found" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">404</h1>
          <h2 className="error-title">Page Not Found</h2>
          <p className="error-message">
            The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
          </p>
          <Link href="/" className="btn btn-primary">
            Back to Home
          </Link>
        </div>
      </div>
    </Layout>
  );
}

