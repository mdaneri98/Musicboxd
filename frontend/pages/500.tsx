import Link from 'next/link';
import { Layout } from '@/components/layout';

export default function Custom500() {
  return (
    <Layout title="500 - Server Error" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">500</h1>
          <h2 className="error-title">Internal Server Error</h2>
          <p className="error-message">
            Something went wrong on our end. We're working to fix the issue. Please try again later.
          </p>
          <Link href="/" className="btn btn-primary">
            Back to Home
          </Link>
        </div>
      </div>
    </Layout>
  );
}

