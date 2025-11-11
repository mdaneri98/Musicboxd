import Link from 'next/link';
import { Layout } from '@/components/layout';

export default function Custom403() {
  return (
    <Layout title="403 - Forbidden" showSidebar={false}>
      <div className="error-page">
        <div className="error-content">
          <h1 className="error-code">403</h1>
          <h2 className="error-title">Access Forbidden</h2>
          <p className="error-message">
            You don't have permission to access this resource. Please contact the administrator if you believe this is an error.
          </p>
          <Link href="/" className="btn btn-primary">
            Back to Home
          </Link>
        </div>
      </div>
    </Layout>
  );
}

