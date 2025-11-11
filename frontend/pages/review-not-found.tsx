import Link from 'next/link';
import { Layout } from '@/components/layout';

export default function ReviewNotFound() {
  return (
    <Layout title="Review Not Found" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">404</h1>
          <h2 className="error-title">Review Not Found</h2>
          <p className="error-message">
            The review you are looking for does not exist or has been removed.
          </p>
          <Link href="/" className="btn btn-primary">
            Back to Home
          </Link>
        </div>
      </div>
    </Layout>
  );
}

